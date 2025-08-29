package org.spring.projectjs.JPA.App.Repository;

import org.spring.projectjs.JPA.App.Entity.ShopItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ShopItemRepository extends JpaRepository<ShopItem, Long> {
    // 테마별 아이템 조회
    List<ShopItem> findByThema(String thema);
}
