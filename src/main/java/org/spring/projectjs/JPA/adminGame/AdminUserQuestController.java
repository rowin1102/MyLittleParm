package org.spring.projectjs.JPA.adminGame;

import lombok.RequiredArgsConstructor;
import org.spring.projectjs.JPA.App.Entity.EUserQuestId;
import org.spring.projectjs.JPA.App.Entity.UserQuest;
import org.spring.projectjs.JPA.App.Repository.UserQuestRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/** 유저 퀘스트 상태 관리자 */
@RestController
@RequestMapping("/api/admin/user-quest")
@RequiredArgsConstructor
public class AdminUserQuestController {

    private final UserQuestRepository userQuestRepository;

    /** 단건 조회 (복합키) */
    @GetMapping("/{userIdx}/{questId}")
    public ResponseEntity<UserQuest> getUserQuest(
            @PathVariable Long userIdx,
            @PathVariable Long questId) {

        var id = new EUserQuestId(userIdx, questId);
        return userQuestRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /** 상태값만 수정 (예: NONE → DONE). 필드명은 엔티티에 맞춰 변경 필요 */
    @PutMapping("/{userIdx}/{questId}")
    public ResponseEntity<UserQuest> updateQuestStatus(
            @PathVariable Long userIdx,
            @PathVariable Long questId,
            @RequestBody UpdateReq req) {

        var id = new EUserQuestId(userIdx, questId);
        return userQuestRepository.findById(id)
                .map(q -> {
                    if (req.status() != null) {
                        // ⚠️ 실제 필드/세터명으로 바꿔주세요: setQuestState / setStateCode 등
                        q.setStatus(req.status());
                    }
                    return ResponseEntity.ok(userQuestRepository.save(q));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    /** 요청 DTO */
    public record UpdateReq(String status) {}
}
