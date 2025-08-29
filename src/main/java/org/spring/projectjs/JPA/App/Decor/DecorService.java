package org.spring.projectjs.JPA.App.Decor;

import lombok.RequiredArgsConstructor;
import org.spring.projectjs.JPA.App.Decor.dto.EquipRequest;
import org.spring.projectjs.JPA.App.Decor.dto.EquippedItemDto;
import org.spring.projectjs.JPA.App.Decor.dto.InventoryItemDto;
import org.spring.projectjs.JPA.App.Entity.Member;
import org.spring.projectjs.JPA.App.Entity.UserEquip;
import org.spring.projectjs.JPA.App.Entity.UserEquipId;
import org.spring.projectjs.JPA.App.Repository.DecorQueryRepository;
import org.spring.projectjs.JPA.App.Repository.MemberRepository;
import org.spring.projectjs.JPA.App.Repository.UserEquipRepository;
import org.spring.projectjs.JPA.App.Repository.UserPurchaseRepository;
import org.spring.projectjs.JPA.App.projection.EquippedRow;
import org.spring.projectjs.JPA.App.projection.InventoryRow;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class DecorService {

    private final MemberRepository memberRepository;
    private final DecorQueryRepository decorQueryRepository;
    private final UserEquipRepository userEquipRepository;
    private final UserPurchaseRepository userPurchaseRepository;

    /** Authentication → member_idx */
    private Long requireUserIdx(Authentication auth) {
        if (auth == null || auth.getName() == null) {
            throw new IllegalStateException("인증 정보가 없습니다.");
        }
        final String userId = auth.getName();
        final Member m = memberRepository.findByUserId(userId)
                .orElseThrow(() -> new IllegalStateException("사용자를 찾을 수 없습니다: " + userId));
        return m.getMemberIdx();
    }

    /** 인벤토리(구매 목록 + 장착 여부) — slot 미지정 시 전체 */
    @Transactional(readOnly = true)
    public List<InventoryItemDto> getInventory(Authentication auth, String slot) {
        final Long userIdx = requireUserIdx(auth);

        final List<InventoryRow> rows = (slot == null || slot.isBlank())
                ? decorQueryRepository.findInventoryAll(userIdx)
                : decorQueryRepository.findInventoryBySlot(userIdx, slot);

        return rows.stream()
                .map(r -> InventoryItemDto.builder()
                        .itemId(r.getItemId())
                        .itemName(r.getItemName())
                        .description(r.getDescription())
                        .priceGold(r.getPriceGold())
                        .slot(r.getSlot())         // DB의 thema를 slot으로 노출
                        .url(r.getUrl())
                        .equipped("Y".equalsIgnoreCase(r.getEquipped()))
                        .build())
                .collect(Collectors.toList());
    }

    /**
     * 현재 장착 상태 — 중복 착용 허용이므로
     * 슬롯별로 여러 아이템을 반환해야 한다.
     * 반환 형태: Map<slot, List<EquippedItemDto>>
     */
    @Transactional(readOnly = true)
    public Map<String, List<EquippedItemDto>> getEquipped(Authentication auth) {
        final Long userIdx = requireUserIdx(auth);
        final List<EquippedRow> rows = decorQueryRepository.getEquippedWithUrl(userIdx);

        // 같은 slot에 여러 줄이 있을 수 있음 → slot별 그룹핑
        return rows.stream()
                .map(r -> EquippedItemDto.builder()
                        .slot(r.getSlot())
                        .itemId(r.getItemId())
                        .url(r.getUrl())
                        .build())
                .collect(Collectors.groupingBy(
                        EquippedItemDto::getSlot,
                        LinkedHashMap::new,
                        Collectors.toList()
                ));
    }

    /** 장착(중복 허용): 이미 있으면 패스, 없으면 INSERT */
    public void equip(Authentication auth, EquipRequest req) {
        final Long userIdx = requireUserIdx(auth);

        final Long itemId = Optional.ofNullable(req.getItemId())
                .orElseThrow(() -> new IllegalArgumentException("itemId가 필요합니다."));

        // 아이템 존재/소유 여부 검증
        if (decorQueryRepository.existsItem(itemId) == 0L) {
            throw new IllegalArgumentException("존재하지 않는 아이템입니다.");
        }
        if (!userPurchaseRepository.existsByUserIdxAndItemId(userIdx, itemId)) {
            throw new IllegalArgumentException("해당 아이템을 소유하고 있지 않습니다.");
        }

        // 아이템의 슬롯 조회
        final String dbSlot = decorQueryRepository.findSlotByItemId(itemId);
        if (dbSlot == null) throw new IllegalArgumentException("아이템 슬롯 정보를 찾을 수 없습니다.");

        // 요청 slot이 오면 검증
        if (req.getSlot() != null && !req.getSlot().isBlank() && !dbSlot.equals(req.getSlot())) {
            throw new IllegalArgumentException("요청 slot과 아이템의 thema가 일치하지 않습니다.");
        }

        // 중복 착용 허용 → (userIdx, dbSlot, itemId) 행이 없으면 INSERT
        final boolean exists = userEquipRepository
                .existsByIdUserIdxAndIdSlotAndIdItemId(userIdx, dbSlot, itemId);

        if (!exists) {
            final UserEquip ue = UserEquip.builder()
                    .id(new UserEquipId(userIdx, dbSlot, itemId))
                    .equippedAt(Timestamp.from(Instant.now()))
                    .build();
            userEquipRepository.save(ue);
        }
    }

    /**
     * 해제:
     * - itemId가 있으면 해당 아이템만 해제
     * - itemId가 없으면 해당 슬롯 전체 해제
     */
    public void unequip(Authentication auth, String slot, Long itemId) {
        final Long userIdx = requireUserIdx(auth);
        if (slot == null || slot.isBlank()) return;

        if (itemId != null) {
            userEquipRepository.deleteOne(userIdx, slot, itemId);
        } else {
            userEquipRepository.deleteAllInSlot(userIdx, slot);
        }
    }
}
