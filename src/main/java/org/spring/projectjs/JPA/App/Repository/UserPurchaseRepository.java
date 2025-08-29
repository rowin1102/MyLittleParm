package org.spring.projectjs.JPA.App.Repository;

import org.spring.projectjs.JPA.App.Entity.UserPurchase;
import org.spring.projectjs.JPA.App.Entity.UserPurchaseId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface UserPurchaseRepository extends JpaRepository<UserPurchase, UserPurchaseId> {

    // 현재 로그인 유저의 구매 내역 (userIdx는 Service에서 토큰에서 추출 → 여기 파라미터로 전달)
    List<UserPurchase> findByUserIdx(Long userIdx);

    // 특정 아이템을 이미 구매했는지 체크
    boolean existsByUserIdxAndItemId(Long userIdx, Long itemId);

    // 유저가 구매한 아이템 아이디만 가져오기
    @Query("SELECT up.itemId FROM UserPurchase up WHERE up.userIdx = :userIdx")
    List<Long> findPurchasedItemIds(Long userIdx);
}
