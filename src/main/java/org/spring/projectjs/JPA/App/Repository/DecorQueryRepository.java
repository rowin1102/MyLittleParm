package org.spring.projectjs.JPA.App.Repository;

import org.spring.projectjs.JPA.App.Entity.ShopItem;
import org.spring.projectjs.JPA.App.projection.EquippedRow;
import org.spring.projectjs.JPA.App.projection.InventoryRow;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.Repository;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface DecorQueryRepository extends Repository<ShopItem, Long> {

    @Query("""
        select 
           si.itemId       as itemId,
           si.itemName     as itemName,
           si.description  as description,
           si.priceGold    as priceGold,
           si.thema        as slot,
           si.url          as url,
           case when exists (
                 select ue from UserEquip ue
                 where ue.id.userIdx = :userIdx and ue.id.itemId = si.itemId
           ) then 'Y' else 'N' end as equipped
        from ShopItem si
        where exists (
            select up from UserPurchase up
            where up.userIdx = :userIdx and up.itemId = si.itemId
        )
        order by si.thema asc, si.itemId asc
    """)
    List<InventoryRow> findInventoryAll(@Param("userIdx") Long userIdx);

    @Query("""
        select 
           si.itemId       as itemId,
           si.itemName     as itemName,
           si.description  as description,
           si.priceGold    as priceGold,
           si.thema        as slot,
           si.url          as url,
           case when exists (
                 select ue from UserEquip ue 
                 where ue.id.userIdx = :userIdx and ue.id.itemId = si.itemId
           ) then 'Y' else 'N' end as equipped
        from ShopItem si
        where si.thema = :slot
          and exists (
            select up from UserPurchase up
            where up.userIdx = :userIdx and up.itemId = si.itemId
        )
        order by si.itemId asc
    """)
    List<InventoryRow> findInventoryBySlot(@Param("userIdx") Long userIdx, @Param("slot") String slot);

    @Query("""
        select 
           ue.id.slot  as slot,
           ue.id.itemId as itemId,
           si.url   as url
        from UserEquip ue
        join ShopItem si on si.itemId = ue.id.itemId
        where ue.id.userIdx = :userIdx
        order by ue.id.slot asc, ue.equippedAt desc
    """)
    List<EquippedRow> getEquippedWithUrl(@Param("userIdx") Long userIdx);

    @Query("select count(si) from ShopItem si where si.itemId = :itemId")
    long existsItem(@Param("itemId") Long itemId);

    @Query("""
        select count(up) 
        from UserPurchase up 
        where up.userIdx = :userIdx and up.itemId = :itemId
    """)
    long countOwnership(@Param("userIdx") Long userIdx, @Param("itemId") Long itemId);

    @Query("select si.thema from ShopItem si where si.itemId = :itemId")
    String findSlotByItemId(@Param("itemId") Long itemId);
}
