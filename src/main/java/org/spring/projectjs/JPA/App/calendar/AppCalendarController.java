// src/main/java/org/spring/projectjs/JPA/App/calendar/AppCalendarController.java
package org.spring.projectjs.JPA.App.calendar;

import lombok.RequiredArgsConstructor;
import org.spring.projectjs.JPA.App.Entity.AppCalendarPhoto;
import org.spring.projectjs.JPA.App.Repository.MemberRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/calendar")
@CrossOrigin(origins = "*")
public class AppCalendarController {

    private final AppCalendarService calendarService;
    private final MemberRepository memberRepository;

    private static final ZoneId ZONE = ZoneId.of("Asia/Seoul");
    private static final DateTimeFormatter DAY_FMT = DateTimeFormatter.ISO_LOCAL_DATE; // YYYY-MM-DD

    /* ============== 사진 업로드(메타 저장, 하루 1장 치환) ============== */
    @PostMapping("/photos")
    public ResponseEntity<Map<String, Object>> uploadPhoto(
            Authentication auth,
            @RequestBody AppCalendarService.PhotoUploadRequest body) {

        String userId = auth.getName();
        Long userIdx = memberRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("Member not found")).getMemberIdx();

        AppCalendarService.PhotoSaveResult result = calendarService.savePhoto(userIdx, body);
        AppCalendarPhoto saved = result.photo();

        Map<String, Object> res = new HashMap<>();
        res.put("success", true);
        res.put("photo_id", saved.getPhotoId());

        // ✅ java.sql.Date도 안전하게 처리 (toInstant() 금지)
        java.util.Date dk = saved.getDayKey();
        String dayKey;
        if (dk instanceof java.sql.Date) {
            dayKey = ((java.sql.Date) dk).toLocalDate().format(DAY_FMT);
        } else {
            dayKey = dk.toInstant().atZone(ZONE).toLocalDate().format(DAY_FMT);
        }
        res.put("day_key", dayKey);
        // 클라가 window_key를 읽는다면 같이 내려주기
        res.put("window_key", dayKey);

        res.put("taken_at", saved.getTakenAt());
        res.put("file_path", saved.getFilePath());
        res.put("file_name", saved.getFileName());
        res.put("content_type", saved.getContentType());
        res.put("file_size", saved.getFileSize());
        res.put("created_at", saved.getCreatedAt());

        // 퀘스트 완료 여부 및 보상 후 스탯
        res.put("photo_quest_completed", result.photoQuestCompleted());
        if (result.photoQuestCompleted()) {
            res.put("game_point", result.gamePoint());
            res.put("game_exp", result.gameExp());
            res.put("game_level", result.gameLevel());
            res.put("quest_message", "사진을 올렸습니다. 일일 사진 퀘스트 완료!");
        }

        return ResponseEntity.ok(res);
    }


    /* ============== 출석 체크 ============== */
    @PostMapping("/attendance/check-in")
    public ResponseEntity<Map<String, Object>> checkIn(
            Authentication auth,
            @RequestParam(value = "source", required = false) String source,
            @RequestParam(value = "memo", required = false) String memo) {

        String userId = auth.getName();
        Long userIdx = memberRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("Member not found")).getMemberIdx();

        AppCalendarService.AttendanceResult result = calendarService.checkIn(userIdx, source, memo);

        Map<String, Object> res = new HashMap<>();
        res.put("success", true);
        res.put("first_check_today", result.firstCheckToday());
        res.put("window_key", result.windowKey()); // YYYY-MM-DD
        res.put("game_point", result.gamePoint());
        res.put("game_exp", result.gameExp());
        res.put("game_level", result.gameLevel());
        return ResponseEntity.ok(res);
    }
}
