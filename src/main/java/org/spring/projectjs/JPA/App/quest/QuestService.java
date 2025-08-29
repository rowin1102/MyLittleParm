// src/main/java/org/spring/projectjs/JPA/App/quest/QuestService.java
package org.spring.projectjs.JPA.App.quest;

import lombok.RequiredArgsConstructor;
import org.spring.projectjs.JPA.App.Entity.*;
import org.spring.projectjs.JPA.App.Repository.MemberRepository;
import org.spring.projectjs.JPA.App.Repository.QuestRepository;
import org.spring.projectjs.JPA.App.Repository.UserQuestRepository;
import org.spring.projectjs.JPA.App.Repository.AppCalendarPhotoRepository; // ★ 추가
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

@Service
@RequiredArgsConstructor
public class QuestService {

    private final QuestRepository questRepository;
    private final MemberRepository memberRepository;
    private final UserQuestRepository userQuestRepository;
    private final AppCalendarPhotoRepository appCalendarPhotoRepository; // ★ 추가

    private static final ZoneId ZONE = ZoneId.of("Asia/Seoul");

    /* ================= 조회 ================= */

    @Transactional(readOnly = true)
    public List<Quest> listAll() {
        return questRepository.findAll();
    }

    @Transactional(readOnly = true)
    public List<UserQuest> listUserQuests(Long userIdx) {
        // quest 즉시 로딩(프록시 직렬화 방지) - 레포에 @EntityGraph 붙어 있음
        return userQuestRepository.findByIdUserIdx(userIdx);
    }

    /* ================= 시작 ================= */

    @Transactional
    public void startQuest(Long userIdx, Long questId) {
        Member m = memberRepository.findById(userIdx)
                .orElseThrow(() -> new IllegalArgumentException("Member not found: " + userIdx));
        Quest q = questRepository.findById(questId)
                .orElseThrow(() -> new IllegalArgumentException("Quest not found: " + questId));

        EUserQuestId id = new EUserQuestId(userIdx, questId);
        if (userQuestRepository.existsById(id)) return;

        Date now = new Date();
        UserQuest uq = new UserQuest();
        uq.setId(id);
        uq.setMember(m);
        uq.setQuest(q);
        uq.setStatus("n");
        uq.setWindowKey(null);
        uq.setCompletedAt(null);
        uq.setProgressCount(0);
        uq.setProgressJson(null);
        uq.setCreatedAt(now);
        uq.setUpdatedAt(now);
        userQuestRepository.save(uq);
    }

    /* ============== 공통: 하루창(윈도우) 보정 ============== */

    /** yyyy-MM-dd 형태(KST) */
    private String todayKey() {
        return LocalDate.now(ZONE).toString();
    }

    /** 캘린더 dayKey(Date) (오라클 DATE와 맞춤) */
    private Date todayDayKeyDate() {
        // java.sql.Date는 시간부 00:00:00
        return java.sql.Date.valueOf(LocalDate.now(ZONE));
    }

    /* ================= 일반 완료 (타입별 조건 검증 추가) ================= */

    @Transactional
    public Member completeQuest(Long userIdx, Long questId) {
        Member m = memberRepository.findById(userIdx)
                .orElseThrow(() -> new IllegalArgumentException("Member not found: " + userIdx));
        Quest q = questRepository.findById(questId)
                .orElseThrow(() -> new IllegalArgumentException("Quest not found: " + questId));

        EUserQuestId id = new EUserQuestId(userIdx, questId);

        // ★ 락 걸고 가져오기 (없으면 생성)
        UserQuest uq = userQuestRepository.findByIdForUpdate(id).orElse(null);
        if (uq == null) {
            Date now = new Date();
            uq = new UserQuest();
            uq.setId(id);
            uq.setMember(m);
            uq.setQuest(q);
            uq.setStatus("n");
            uq.setWindowKey(null);
            uq.setCompletedAt(null);
            uq.setProgressCount(0);
            uq.setProgressJson(null);
            uq.setCreatedAt(now);
            uq.setUpdatedAt(now);
            userQuestRepository.save(uq);
        }

        // ★ 하루창 리셋
        String tKey = todayKey();
        if (uq.getWindowKey() == null || !tKey.equals(uq.getWindowKey())) {
            userQuestRepository.resetOne(userIdx, questId, tKey);
            uq = userQuestRepository.findByIdForUpdate(id).orElseThrow();
        }

        // 이미 y면 중복 보상 방지
        if ("y".equalsIgnoreCase(uq.getStatus())) {
            return memberRepository.findById(userIdx).orElseThrow();
        }

        // ★ 타입별 완료 조건 검증
        QuestType type = q.getQuestType();
        if (type == QuestType.DAILY_PHOTO) {
            // 오늘(KST) 업로드 여부 확인 (deleted=0)
            boolean ok = appCalendarPhotoRepository
                    .existsByUserIdxAndDayKeyAndDeleted(userIdx, todayDayKeyDate(), 0);
            if (!ok) {
                throw new IllegalStateException("오늘 캘린더 사진이 없습니다.");
            }
        } else if (type == QuestType.DAILY_ATTENDANCE) {
            // 출석은 별도 조건 없음(하루 1회는 창 리셋 + status로 제한)
        } else {
            // 다른 타입은 필요 조건이 있으면 여기서 체크
        }

        // 상태 y로 마킹(멱등)
        int changed = userQuestRepository.markCompletedIfNotYet(userIdx, questId);
        if (changed == 1) {
            applyRewards(m, q);
            memberRepository.save(m);
        }

        return memberRepository.findById(userIdx).orElseThrow();
    }

    /* ================= 출석(일일 멱등) ================= */

    @Transactional
    public Member checkInAttendance(Long userIdx) {
        Member m = memberRepository.findById(userIdx)
                .orElseThrow(() -> new IllegalArgumentException("Member not found: " + userIdx));

        Quest attendance = questRepository.findByQuestTypeAndActive(QuestType.DAILY_ATTENDANCE, true)
                .orElseThrow(() -> new IllegalStateException("Attendance quest not found or inactive"));

        String tKey = todayKey();
        EUserQuestId id = new EUserQuestId(userIdx, attendance.getQuestId());

        // ★ 락 + 생성/리셋
        UserQuest uq = userQuestRepository.findByIdForUpdate(id).orElse(null);
        if (uq == null) {
            Date now = new Date();
            uq = new UserQuest();
            uq.setId(id);
            uq.setMember(m);
            uq.setQuest(attendance);
            uq.setStatus("n");
            uq.setWindowKey(tKey);
            uq.setCompletedAt(null);
            uq.setProgressCount(0);
            uq.setProgressJson(null);
            uq.setCreatedAt(now);
            uq.setUpdatedAt(now);
            userQuestRepository.save(uq);
        } else if (!tKey.equals(uq.getWindowKey())) {
            userQuestRepository.resetOne(userIdx, attendance.getQuestId(), tKey);
        }

        int changed = userQuestRepository.markCompletedIfNotYet(userIdx, attendance.getQuestId());
        if (changed == 1) {
            applyRewards(m, attendance);
            memberRepository.save(m);
        }
        return memberRepository.findById(userIdx).orElseThrow();
    }

    /* ========== 사진 저장 이벤트 훅(일일 멱등) ========== */

    @Transactional
    public boolean onPhotoSavedGiveRewardIfAny(Long userIdx) {
        AtomicBoolean completed = new AtomicBoolean(false);

        questRepository.findByQuestTypeAndActive(QuestType.DAILY_PHOTO, true).ifPresent(photoQuest -> {
            Member m = memberRepository.findById(userIdx)
                    .orElseThrow(() -> new IllegalArgumentException("Member not found: " + userIdx));

            String tKey = todayKey();
            EUserQuestId id = new EUserQuestId(userIdx, photoQuest.getQuestId());

            // ★ 락 + 생성/리셋
            UserQuest uq = userQuestRepository.findByIdForUpdate(id).orElse(null);
            if (uq == null) {
                Date now = new Date();
                uq = new UserQuest();
                uq.setId(id);
                uq.setMember(m);
                uq.setQuest(photoQuest);
                uq.setStatus("n");
                uq.setWindowKey(tKey);
                uq.setCompletedAt(null);
                uq.setProgressCount(0);
                uq.setProgressJson(null);
                uq.setCreatedAt(now);
                uq.setUpdatedAt(now);
                userQuestRepository.save(uq);
            } else if (!tKey.equals(uq.getWindowKey())) {
                userQuestRepository.resetOne(userIdx, photoQuest.getQuestId(), tKey);
            }

            int changed = userQuestRepository.markCompletedIfNotYet(userIdx, photoQuest.getQuestId());
            if (changed == 1) {
                applyRewards(m, photoQuest);
                memberRepository.save(m);
                completed.set(true);
            }
        });

        return completed.get();
    }

    /* ================= 내부 유틸 ================= */

    private void applyRewards(Member m, Quest q) {
        // 포인트
        long addPoint = n(q.getRewardGold());
        m.setGamePoint(n(m.getGamePoint()) + addPoint);

        // 경험치/레벨
        long expGain = n(q.getRewardExp());
        long exp = n(m.getGameExp()) + expGain;
        long lvl = n(m.getGameLevel());

        while (exp >= neededExp(lvl)) {
            exp -= neededExp(lvl);
            lvl += 1;
        }

        m.setGameExp(exp);
        m.setGameLevel(lvl);
    }

    private long neededExp(long level) {
        return level * 100;
    }

    private static long n(Long v) { return v == null ? 0L : v; }
}
