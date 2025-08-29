// src/main/java/org/spring/projectjs/JPA/App/Repository/AttendanceLogRepository.java
package org.spring.projectjs.JPA.App.Repository;

import org.spring.projectjs.JPA.App.Entity.AttendanceLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface AttendanceLogRepository extends JpaRepository<AttendanceLog, Long> {

    boolean existsByUserIdxAndWindowKey(Long userIdx, String windowKey);

    Optional<AttendanceLog> findByUserIdxAndWindowKey(Long userIdx, String windowKey);

    Page<AttendanceLog> findByUserIdxOrderByCheckedAtDesc(Long userIdx, Pageable pageable);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query(value = """
           MERGE INTO attendance_logs t
           USING (SELECT :userIdx AS user_idx, :windowKey AS window_key FROM dual) s
              ON (t.user_idx = s.user_idx AND t.window_key = s.window_key)
           WHEN NOT MATCHED THEN
             INSERT (user_idx, window_key, checked_at, source, created_at)
             VALUES (:userIdx, :windowKey, SYSTIMESTAMP, :source, SYSTIMESTAMP)
           """, nativeQuery = true)
    int insertIfAbsentMerge(@Param("userIdx") Long userIdx,
                            @Param("windowKey") String windowKey,
                            @Param("source") String source);

    @Query(value = """
           SELECT COUNT(DISTINCT al.window_key)
             FROM attendance_logs al
            WHERE al.user_idx = :userIdx
              AND SUBSTR(al.window_key,1,7) = :monthKey
           """, nativeQuery = true)
    int countAttendanceDaysInMonth(@Param("userIdx") Long userIdx, @Param("monthKey") String monthKey);
}
