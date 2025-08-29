package org.spring.projectjs.JPA.App.Repository;
import org.springframework.data.repository.query.Param;
import org.spring.projectjs.JPA.App.Entity.UserEquip;
import org.spring.projectjs.JPA.App.Entity.UserEquipId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface UserEquipRepository extends JpaRepository<UserEquip, UserEquipId> {

    List<UserEquip> findByIdUserIdx(Long userIdx);
    List<UserEquip> findByIdUserIdxAndIdSlot(Long userIdx, String slot);

    boolean existsByIdUserIdxAndIdSlotAndIdItemId(Long userIdx, String slot, Long itemId);

    @Modifying
    @Query("""
      delete from UserEquip ue
       where ue.id.userIdx = :userIdx
         and ue.id.slot    = :slot
         and ue.id.itemId  = :itemId
    """)
    int deleteOne(@Param("userIdx") Long userIdx,
                  @Param("slot") String slot,
                  @Param("itemId") Long itemId);

    @Modifying
    @Query("""
      delete from UserEquip ue
       where ue.id.userIdx = :userIdx
         and ue.id.slot    = :slot
    """)
    int deleteAllInSlot(@Param("userIdx") Long userIdx,
                        @Param("slot") String slot);
}
