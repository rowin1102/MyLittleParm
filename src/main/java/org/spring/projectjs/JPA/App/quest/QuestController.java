// src/main/java/org/spring/projectjs/JPA/App/quest/QuestController.java
package org.spring.projectjs.JPA.App.quest;

import lombok.RequiredArgsConstructor;
import org.spring.projectjs.JPA.App.Entity.Member;
import org.spring.projectjs.JPA.App.Entity.Quest;
import org.spring.projectjs.JPA.App.Entity.UserQuest;
import org.spring.projectjs.JPA.App.Repository.MemberRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/quest")
@CrossOrigin(origins = "*")
public class QuestController {

    private final QuestService questService;
    private final MemberRepository memberRepository;

    /** 현재 로그인 사용자의 member_idx 조회 */
    private Long userIdx(Authentication auth) {
        if (auth == null) throw new IllegalArgumentException("Unauthenticated");
        String userId = auth.getName();
        return memberRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("Member not found: " + userId))
                .getMemberIdx();
    }

    /** 전체(또는 활성) 퀘스트 목록 */
    @GetMapping
    public ResponseEntity<List<Quest>> listQuests() {
        return ResponseEntity.ok(questService.listAll());
    }

    /** 내 퀘스트 목록 (quest 즉시 로딩됨) */
    @GetMapping("/me")
    public ResponseEntity<List<UserQuest>> myQuests(Authentication auth) {
        return ResponseEntity.ok(questService.listUserQuests(userIdx(auth)));
    }

    /** 퀘스트 시작(멱등) */
    @PostMapping("/{questId}/start")
    public ResponseEntity<Map<String, Object>> start(Authentication auth, @PathVariable Long questId) {
        Long uid = userIdx(auth);
        questService.startQuest(uid, questId);
        return ResponseEntity.ok(Map.of("success", true, "message", "퀘스트 시작"));
    }

    /** 퀘스트 완료(타입별 조건 검증 + 보상적용) */
    @PostMapping("/{questId}/complete")
    public ResponseEntity<Map<String, Object>> complete(Authentication auth, @PathVariable Long questId) {
        Long uid = userIdx(auth);
        Member updated = questService.completeQuest(uid, questId);
        Map<String, Object> body = new HashMap<>();
        body.put("success", true);
        body.put("game_point", updated.getGamePoint());
        body.put("game_exp", updated.getGameExp());
        body.put("game_level", updated.getGameLevel());
        return ResponseEntity.ok(body);
    }

    /** 출석 체크(일일 멱등) */
    @PostMapping("/attendance/check-in")
    public ResponseEntity<Map<String, Object>> attendance(Authentication auth) {
        Long uid = userIdx(auth);
        Member updated = questService.checkInAttendance(uid);
        Map<String, Object> body = new HashMap<>();
        body.put("success", true);
        body.put("game_point", updated.getGamePoint());
        body.put("game_exp", updated.getGameExp());
        body.put("game_level", updated.getGameLevel());
        return ResponseEntity.ok(body);
    }

    /* ===================== 관리자 전용 유틸 API ===================== */

    /** 특정 유저 퀘스트 목록 조회 (관리자만) */
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/user/{userIdx}")
    public ResponseEntity<List<UserQuest>> listByUser(@PathVariable Long userIdx) {
        return ResponseEntity.ok(questService.listUserQuests(userIdx));
    }

    /** 특정 유저에 대해 퀘스트 시작 (관리자만) */
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/{questId}/start/{userIdx}")
    public ResponseEntity<Map<String, Object>> startByUser(@PathVariable Long questId, @PathVariable Long userIdx) {
        questService.startQuest(userIdx, questId);
        return ResponseEntity.ok(Map.of("success", true));
    }

    /** 특정 유저에 대해 퀘스트 완료 (관리자만) */
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/{questId}/complete/{userIdx}")
    public ResponseEntity<Map<String, Object>> completeByUser(@PathVariable Long questId, @PathVariable Long userIdx) {
        Member updated = questService.completeQuest(userIdx, questId);
        return ResponseEntity.ok(Map.of(
                "success", true,
                "game_point", updated.getGamePoint(),
                "game_exp", updated.getGameExp(),
                "game_level", updated.getGameLevel()
        ));
    }
}
