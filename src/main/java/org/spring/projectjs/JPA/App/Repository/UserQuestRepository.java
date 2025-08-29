// src/main/java/org/spring/projectjs/JPA/App/Repository/UserQuestRepository.java
package org.spring.projectjs.JPA.App.Repository;

import org.spring.projectjs.JPA.App.Entity.EUserQuestId;
import org.spring.projectjs.JPA.App.Entity.UserQuest;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;

import jakarta.persistence.LockModeType;
import java.util.List;
import java.util.Optional;

public interface UserQuestRepository extends JpaRepository<UserQuest, EUserQuestId> {

    // ★ quest를 즉시 로딩해서 프록시가 반환되지 않도록
    @EntityGraph(attributePaths = {"quest"})
    List<UserQuest> findByIdUserIdx(Long userIdx);

    @EntityGraph(attributePaths = {"quest"})
    Optional<UserQuest> findByIdUserIdxAndIdQuestId(Long userIdx, Long questId);

    boolean existsById(EUserQuestId id);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select uq from UserQuest uq where uq.id = :id")
    Optional<UserQuest> findByIdForUpdate(@Param("id") EUserQuestId id);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("""
           UPDATE UserQuest uq
              SET uq.status = 'y',
                  uq.completedAt = CURRENT_TIMESTAMP,
                  uq.updatedAt = CURRENT_TIMESTAMP
            WHERE uq.id.userIdx = :userIdx
              AND uq.id.questId = :questId
              AND uq.status = 'n'
           """)
    int markCompletedIfNotYet(@Param("userIdx") Long userIdx, @Param("questId") Long questId);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("""
           UPDATE UserQuest uq
              SET uq.status = 'n',
                  uq.completedAt = null,
                  uq.windowKey = :todayKey,
                  uq.progressCount = 0,
                  uq.progressJson = null,
                  uq.updatedAt = CURRENT_TIMESTAMP
            WHERE uq.id.userIdx = :userIdx
              AND uq.id.questId = :questId
           """)
    int resetOne(@Param("userIdx") Long userIdx,
                 @Param("questId") Long questId,
                 @Param("todayKey") String todayKey);
}
