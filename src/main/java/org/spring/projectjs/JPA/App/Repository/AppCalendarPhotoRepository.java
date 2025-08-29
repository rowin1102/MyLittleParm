// src/main/java/org/spring/projectjs/JPA/App/Repository/AppCalendarPhotoRepository.java
package org.spring.projectjs.JPA.App.Repository;

import org.spring.projectjs.JPA.App.Entity.AppCalendarPhoto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;

import java.util.Date;
import java.util.List;
import java.util.Optional;

public interface AppCalendarPhotoRepository extends JpaRepository<AppCalendarPhoto, Long> {

    Page<AppCalendarPhoto> findByUserIdxAndDeletedOrderByTakenAtDesc(Long userIdx, Integer deleted, Pageable pageable);

    Optional<AppCalendarPhoto> findTopByUserIdxAndDayKeyAndDeletedOrderByTakenAtAsc(Long userIdx, Date dayKey, Integer deleted);

    long countByUserIdxAndDayKeyAndDeleted(Long userIdx, Date dayKey, Integer deleted);

    Optional<AppCalendarPhoto> findByUserIdxAndChecksumSha256AndDeleted(Long userIdx, String checksumSha256, Integer deleted);

    List<AppCalendarPhoto> findAllByUserIdxAndDayKeyAndDeletedOrderByTakenAtAsc(Long userIdx, Date dayKey, Integer deleted);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("""
           UPDATE AppCalendarPhoto c
              SET c.deleted = 1,
                  c.updatedAt = CURRENT_TIMESTAMP
            WHERE c.photoId = :photoId
              AND c.userIdx = :userIdx
           """)
    int softDelete(@Param("userIdx") Long userIdx, @Param("photoId") Long photoId);

    @Query(value = """
           SELECT TO_CHAR(p.day_key, 'YYYY-MM-DD') AS dayKey, COUNT(*) AS cnt
             FROM app_calendar_photo p
            WHERE p.user_idx = :userIdx
              AND p.deleted = 0
              AND TO_CHAR(p.day_key, 'YYYY-MM') = :monthKey
            GROUP BY TO_CHAR(p.day_key, 'YYYY-MM-DD')
            ORDER BY 1
           """, nativeQuery = true)
    List<Object[]> countByDayInMonth(@Param("userIdx") Long userIdx, @Param("monthKey") String monthKey);


    boolean existsByUserIdxAndDayKeyAndDeleted(Long userIdx, Date dayKey, Integer deleted);
}
