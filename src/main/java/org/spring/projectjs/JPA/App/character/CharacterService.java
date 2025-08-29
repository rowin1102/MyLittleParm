// src/main/java/.../character/CharacterService.java
package org.spring.projectjs.JPA.App.character;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.spring.projectjs.JPA.App.Entity.Character;
import org.spring.projectjs.JPA.App.Repository.CharacterRepository;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
@RequiredArgsConstructor
@Transactional
public class CharacterService {

    private final CharacterRepository characterRepository;

    /** 유저 캐릭터 조회(없으면 생성). 이름은 null, stage는 레벨→스테이지 규칙으로 초기화 */
    public Character getOrCreate(long userIdx, int userLevel) {
        return characterRepository.findByUserIdx(userIdx)
                .orElseGet(() -> characterRepository.save(
                        Character.builder()
                                .userIdx(userIdx)
                                .charName(null)                 // 최초엔 이름 없음
                                .stage(levelToStage(userLevel)) // 레벨 기반 초기 스테이지
                                .createdAt(new Date())
                                .updatedAt(new Date())
                                .build()
                ));
    }

    /** 이름 최초 1회 등록 (이미 있으면 예외) */
    public Character registerNameOnce(long userIdx, String rawName) {
        var c = characterRepository.findByUserIdx(userIdx)
                .orElseThrow(() -> new IllegalStateException("캐릭터가 없습니다. 먼저 /me 호출로 생성하세요."));

        if (c.getCharName() != null && !c.getCharName().isBlank()) {
            throw new IllegalStateException("이미 이름이 등록된 캐릭터입니다.");
        }

        String name = sanitizeName(rawName);
        if (name.isEmpty()) throw new IllegalArgumentException("이름은 비어 있을 수 없습니다.");

        c.setCharName(name);
        c.setUpdatedAt(new Date());
        return characterRepository.save(c);
    }

    /** 레벨 변화 시 스테이지 자동 동기화 (변경시에만 저장) */
    public Character syncStageFromLevel(long userIdx, int userLevel) {
        var c = characterRepository.findByUserIdx(userIdx)
                .orElseThrow(() -> new IllegalStateException("캐릭터가 없습니다."));

        int newStage = levelToStage(userLevel);
        if (newStage != c.getStage()) {
            c.setStage(newStage);
            c.setUpdatedAt(new Date());
            return characterRepository.save(c);
        }
        return c;
    }

    /** 프론트 표현 키 */
    public String faceKeyOf(int stage) {
        return switch (stage) {
            case 4 -> "max";
            case 3 -> "grow";
            case 2 -> "sprout";
            default -> "seed";
        };
    }

    // --- 내부 유틸 ---

    private String sanitizeName(String raw) {
        if (raw == null) return "";
        String n = raw.trim();
        return n.length() <= 50 ? n : n.substring(0, 50);
    }

    /** 레벨→스테이지 매핑 (원하면 임계값만 바꾸면 됨) */
    private int levelToStage(int level) {
        if (level >= 30) return 4;
        if (level >= 20) return 3;
        if (level >= 10) return 2;
        return 1;
    }
}
