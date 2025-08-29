// src/main/java/.../character/CharacterController.java
package org.spring.projectjs.JPA.App.character;

import lombok.RequiredArgsConstructor;
import org.spring.projectjs.JPA.App.Entity.Character;
import org.spring.projectjs.JPA.App.Repository.MemberRepository;
import org.spring.projectjs.auth.JwtTokenProvider;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.LinkedHashMap;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/character")
@CrossOrigin(origins = "*")
public class CharacterController {

    private final CharacterService characterService;     // 이미 A패턴으로 재구현한 서비스 (userIdx, level 받음)
    private final JwtTokenProvider tokenProvider;        // 토큰 → userIdx
    private final MemberRepository memberRepository;     // 최신 레벨 조회용

    private String faceKeyOf(int stage) {
        return switch (stage) {
            case 4 -> "max";
            case 3 -> "grow";
            case 2 -> "sprout";
            default -> "seed";
        };
    }

    /** 내 캐릭터: 없으면 생성, 이름 미등록이면 needs_name=true */
    @GetMapping("/me")
    public ResponseEntity<Map<String, Object>> me(@RequestHeader("Authorization") String authHeader) {
        final String token = authHeader.replace("Bearer ", "");
        final long userIdx = tokenProvider.getUserIdx(token);

        final int level = memberRepository.findById(userIdx)
                .map(m -> m.getGameLevel() == null ? 1 : m.getGameLevel().intValue())
                .orElse(1);

        // get-or-create + 레벨 기준 stage 동기화
        Character c = characterService.getOrCreate(userIdx, level);
        c = characterService.syncStageFromLevel(userIdx, level);

        final boolean needsName = (c.getCharName() == null) || c.getCharName().isBlank();

        Map<String, Object> body = new LinkedHashMap<>();
        body.put("success", true);
        body.put("char_id", c.getCharId());
        body.put("user_idx", c.getUserIdx());
        body.put("name", needsName ? "" : c.getCharName()); // 빈문자열로 통일(프론트 모델과 호환)
        body.put("stage", c.getStage());
        body.put("face_key", faceKeyOf(c.getStage()));
        body.put("needs_name", needsName);
        return ResponseEntity.ok(body);
    }

    public record NameReq(String name) {}

    /** 캐릭터 이름 최초 등록(이미 있으면 409) */
    @PostMapping("/name")
    public ResponseEntity<Map<String, Object>> registerName(
            @RequestHeader("Authorization") String authHeader,
            @RequestBody NameReq body) {

        final String token = authHeader.replace("Bearer ", "");
        final long userIdx = tokenProvider.getUserIdx(token);

        if (body == null || body.name() == null || body.name().trim().isEmpty()) {
            Map<String, Object> err = new LinkedHashMap<>();
            err.put("success", false);
            err.put("error", "INVALID_NAME");
            err.put("message", "이름은 비어 있을 수 없습니다.");
            return ResponseEntity.badRequest().body(err);
        }

        try {
            Character c = characterService.registerNameOnce(userIdx, body.name().trim());

            Map<String, Object> resp = new LinkedHashMap<>();
            resp.put("success", true);
            resp.put("name", c.getCharName());
            resp.put("stage", c.getStage());
            resp.put("face_key", faceKeyOf(c.getStage()));
            resp.put("needs_name", false);
            return ResponseEntity.ok(resp);

        } catch (IllegalStateException e) {
            // 이미 등록됨
            Map<String, Object> err = new LinkedHashMap<>();
            err.put("success", false);
            err.put("error", "NAME_ALREADY_REGISTERED");
            err.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.CONFLICT).body(err);
        }
    }
}
