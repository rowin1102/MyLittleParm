package org.spring.projectjs.JPA.App.Decor;

import lombok.RequiredArgsConstructor;
import org.spring.projectjs.JPA.App.Decor.dto.EquipRequest;
import org.spring.projectjs.JPA.App.Decor.dto.EquippedItemDto;
import org.spring.projectjs.JPA.App.Decor.dto.InventoryItemDto;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/decor")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class DecorController {

    private final DecorService decorService;

    /** 인벤토리 조회 (slot 미지정 시 전체) */
    @GetMapping("/inventory")
    public ResponseEntity<List<InventoryItemDto>> getInventory(
            Authentication auth,
            @RequestParam(required = false) String slot
    ) {
        return ResponseEntity.ok(decorService.getInventory(auth, slot));
    }

    /**
     * 현재 장착 상태
     * 반환: { "배경": [{slot,itemId,url}, ...], "벽": [...], ... }
     */
    @GetMapping("/equipped")
    public ResponseEntity<Map<String, List<EquippedItemDto>>> getEquipped(Authentication auth) {
        return ResponseEntity.ok(decorService.getEquipped(auth));
    }

    /** 장착(중복 허용) — body: { itemId, slot(선택) } */
    @PostMapping("/equip")
    public ResponseEntity<Void> equip(Authentication auth, @RequestBody EquipRequest req) {
        decorService.equip(auth, req);
        return ResponseEntity.noContent().build();
    }

    /**
     * 해제:
     * - /equip/{slot}?itemId=123  → 해당 아이템만 해제
     * - /equip/{slot}             → 슬롯 전체 해제
     */
    @DeleteMapping("/equip/{slot}")
    public ResponseEntity<Void> unequip(
            Authentication auth,
            @PathVariable String slot,
            @RequestParam(required = false) Long itemId
    ) {
        decorService.unequip(auth, slot, itemId);
        return ResponseEntity.noContent().build();
    }
}
