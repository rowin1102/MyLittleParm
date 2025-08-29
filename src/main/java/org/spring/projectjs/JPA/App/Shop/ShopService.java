package org.spring.projectjs.JPA.App.Shop;

import lombok.RequiredArgsConstructor;
import org.spring.projectjs.JPA.App.Entity.*;
import org.spring.projectjs.JPA.App.Repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class ShopService {

    private final ShopItemRepository shopItemRepository;
    private final UserPurchaseRepository userPurchaseRepository;
    private final UserFurnitureRepository userFurnitureRepository;
    private final FurnitureRepository furnitureRepository;
    private final MemberRepository memberRepository;

    /** 테마별 아이템 (전체, 구매여부 체크는 클라에서 가능) */
    @Transactional(readOnly = true)
    public List<ShopItem> getItemsByThema(String thema) {
        return shopItemRepository.findByThema(thema);
    }

    /** 내가 산 모든 아이템 */
    @Transactional(readOnly = true)
    public List<UserPurchase> getMyPurchases(Long userIdx) {
        return userPurchaseRepository.findByUserIdx(userIdx);
    }

    /** 아이템 구매 */
    public PurchaseResult purchaseItem(Long userIdx, Long itemId) {
        Optional<ShopItem> opt = shopItemRepository.findById(itemId);
        if (opt.isEmpty()) {
            return new PurchaseResult(false, "존재하지 않는 아이템입니다.", 0);
        }

        ShopItem item = opt.get();
        int price = item.getPriceGold() == null ? 0 : item.getPriceGold();

        Member member = memberRepository.findById(userIdx)
                .orElseThrow(() -> new IllegalArgumentException("유저를 찾을 수 없습니다."));

        if (member.getGamePoint() < price) {
            return new PurchaseResult(false, "골드가 부족합니다.", member.getGamePoint().intValue());
        }

        // 차감 후 저장
        member.setGamePoint(member.getGamePoint() - price);
        memberRepository.save(member);

        // 구매 기록 저장 (중복 구매 허용)
        userPurchaseRepository.save(new UserPurchase(userIdx, itemId, new Date()));

        return new PurchaseResult(true, "구매 완료!", member.getGamePoint().intValue());
    }

    /** 내가 가진 가구 */
    @Transactional(readOnly = true)
    public List<UserFurniture> getMyFurnitures(Long userIdx) {
        return userFurnitureRepository.findByUserIdx(userIdx);
    }

    /** 가구 획득 */
    public boolean acquireFurniture(Long userIdx, Long furnId) {
        if (!furnitureRepository.existsById(furnId)) return false;

        // 중복 보관 허용
        userFurnitureRepository.save(new UserFurniture(userIdx, furnId));
        return true;
    }
}
