// src/main/java/org/spring/projectjs/JPA/App/calendar/AppCalendarService.java
package org.spring.projectjs.JPA.App.calendar;

import lombok.RequiredArgsConstructor;
import org.spring.projectjs.JPA.App.Entity.AppCalendarPhoto;
import org.spring.projectjs.JPA.App.Entity.Member;
import org.spring.projectjs.JPA.App.Repository.AppCalendarRepository;
import org.spring.projectjs.JPA.App.Repository.AppCalendarPhotoRepository;
import org.spring.projectjs.JPA.App.Repository.AttendanceLogRepository;
import org.spring.projectjs.JPA.App.Repository.MemberRepository;
import org.spring.projectjs.JPA.App.quest.QuestService;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AppCalendarService {

    private final AppCalendarPhotoRepository photoRepo;
    private final AppCalendarRepository calendarRepo;
    private final AttendanceLogRepository attendanceLogRepo;

    private final MemberRepository memberRepo;
    private final QuestService questService;

    private static final ZoneId ZONE = ZoneId.of("Asia/Seoul");

    private LocalDate localDateOf(Date when) {
        if (when == null) return LocalDate.now(ZONE);
        return Instant.ofEpochMilli(when.getTime()).atZone(ZONE).toLocalDate();
    }

    private Date sqlDateOf(LocalDate d) {
        return java.sql.Date.valueOf(d);
    }

    /* ================= 사진 저장 (하루 1장: UPDATE 치환) ================= */
    @Transactional
    public PhotoSaveResult savePhoto(Long userIdx, PhotoUploadRequest req) {
        memberRepo.findById(userIdx)
                .orElseThrow(() -> new IllegalArgumentException("Member not found: " + userIdx));

        Date now = new Date();
        Date takenAt = Optional.ofNullable(req.takenAt()).orElse(now);
        LocalDate ld = localDateOf(takenAt);
        java.sql.Date dayKey = (java.sql.Date) sqlDateOf(ld);

        // 1) 활성(deleted=0) 행이 있으면 그걸 갱신 (멱등 체크 포함)
        var activeOpt = photoRepo.findTopByUserIdxAndDayKeyAndDeletedOrderByTakenAtAsc(userIdx, dayKey, 0);
        if (activeOpt.isPresent()) {
            AppCalendarPhoto p = activeOpt.get();

            // 동일 파일(체크섬 동일) → 저장 없이 멱등 처리
            if (req.checksumSha256() != null && req.checksumSha256().equals(p.getChecksumSha256())) {
                boolean completed = questService.onPhotoSavedGiveRewardIfAny(userIdx);
                return buildResult(p, completed, userIdx);
            }

            fillPhoto(p, req, takenAt, now);
            p.setDeleted(0);
            AppCalendarPhoto saved = photoRepo.save(p);

            boolean completed = questService.onPhotoSavedGiveRewardIfAny(userIdx);
            return buildResult(saved, completed, userIdx);
        }

        // 2) 삭제(deleted=1)된 기존 행이 있으면 되살려서 갱신
        var deletedOpt = photoRepo.findTopByUserIdxAndDayKeyAndDeletedOrderByTakenAtAsc(userIdx, dayKey, 1);
        if (deletedOpt.isPresent()) {
            AppCalendarPhoto p = deletedOpt.get();
            fillPhoto(p, req, takenAt, now);
            p.setDeleted(0); // 되살리기
            AppCalendarPhoto saved = photoRepo.save(p);

            boolean completed = questService.onPhotoSavedGiveRewardIfAny(userIdx);
            return buildResult(saved, completed, userIdx);
        }

        // 3) 정말 없으면 INSERT (하루 1행 유니크 제약 가정)
        AppCalendarPhoto photo = AppCalendarPhoto.builder()
                .userIdx(userIdx)
                .dayKey(dayKey)
                .takenAt(takenAt)
                .filePath(req.filePath())
                .fileName(req.fileName())
                .contentType(req.contentType())
                .fileSize(req.fileSize())
                .checksumSha256(req.checksumSha256())
                .width(req.width())
                .height(req.height())
                .tagsJson(req.tagsJson())
                .exifJson(req.exifJson())
                .deleted(0)
                .createdAt(now)
                .updatedAt(now)
                .build();
        try {
            AppCalendarPhoto saved = photoRepo.save(photo);

            boolean completed = questService.onPhotoSavedGiveRewardIfAny(userIdx);
            return buildResult(saved, completed, userIdx);
        } catch (DataIntegrityViolationException dup) {
            // 동시 INSERT 레이스 → 재조회 후 갱신 경로로
            var existing = photoRepo
                    .findTopByUserIdxAndDayKeyAndDeletedOrderByTakenAtAsc(userIdx, dayKey, 0)
                    .orElseGet(() ->
                            photoRepo.findTopByUserIdxAndDayKeyAndDeletedOrderByTakenAtAsc(userIdx, dayKey, 1)
                                    .orElseThrow(() -> dup)
                    );

            fillPhoto(existing, req, takenAt, now);
            existing.setDeleted(0);
            AppCalendarPhoto saved = photoRepo.save(existing);

            boolean completed = questService.onPhotoSavedGiveRewardIfAny(userIdx);
            return buildResult(saved, completed, userIdx);
        }
    }

    private void fillPhoto(AppCalendarPhoto p, PhotoUploadRequest req, Date takenAt, Date now) {
        p.setTakenAt(takenAt);
        p.setFilePath(req.filePath());
        p.setFileName(req.fileName());
        p.setContentType(req.contentType());
        p.setFileSize(req.fileSize());
        p.setChecksumSha256(req.checksumSha256());
        p.setWidth(req.width());
        p.setHeight(req.height());
        p.setTagsJson(req.tagsJson());
        p.setExifJson(req.exifJson());
        p.setUpdatedAt(now);
    }

    private PhotoSaveResult buildResult(AppCalendarPhoto saved, boolean completed, Long userIdx) {
        Long gp = null, gx = null, gl = null;
        if (completed) {
            Member m = memberRepo.findById(userIdx).orElseThrow();
            gp = m.getGamePoint();
            gx = m.getGameExp();
            gl = m.getGameLevel();
        }
        return new PhotoSaveResult(saved, completed, gp, gx, gl);
    }

    /* ================= 출석 체크 ================= */
    @Transactional
    public AttendanceResult checkIn(Long userIdx, String source, String memo) {
        memberRepo.findById(userIdx)
                .orElseThrow(() -> new IllegalArgumentException("Member not found: " + userIdx));

        LocalDate today = LocalDate.now(ZONE);
        Date todaySql = sqlDateOf(today);
        String todayKey = today.toString(); // YYYY-MM-DD

        int insertedOrUpdated = calendarRepo.checkInIfAbsentMerge(userIdx, todaySql);

        if (attendanceLogRepo != null) {
            attendanceLogRepo.insertIfAbsentMerge(userIdx, todayKey,
                    Optional.ofNullable(source).orElse("manual"));
        }

        Member updated = questService.checkInAttendance(userIdx);

        boolean firstCheckToday = false; // 필요 시 insertedOrUpdated 활용해 판정 로직 확장 가능
        return new AttendanceResult(
                firstCheckToday,
                todayKey,
                updated.getGamePoint(),
                updated.getGameExp(),
                updated.getGameLevel()
        );
    }

    /* ================= DTOs ================= */
    public record PhotoUploadRequest(
            Date takenAt,
            String filePath,
            String fileName,
            String contentType,
            Long fileSize,
            String checksumSha256,
            Integer width,
            Integer height,
            String tagsJson,
            String exifJson
    ) {}

    public record PhotoSaveResult(
            AppCalendarPhoto photo,
            boolean photoQuestCompleted,
            Long gamePoint,
            Long gameExp,
            Long gameLevel
    ) {}

    public record AttendanceResult(
            boolean firstCheckToday,
            String windowKey,  // YYYY-MM-DD
            Long gamePoint,
            Long gameExp,
            Long gameLevel
    ) {}
}
