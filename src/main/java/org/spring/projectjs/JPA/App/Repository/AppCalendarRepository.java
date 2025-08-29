// src/main/java/org/spring/projectjs/JPA/App/Repository/AppCalendarRepository.java
package org.spring.projectjs.JPA.App.Repository;

import org.spring.projectjs.JPA.App.Entity.AppCalendar;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;

import java.util.Date;
import java.util.Optional;

public interface AppCalendarRepository extends JpaRepository<AppCalendar, Long> {

    Optional<AppCalendar> findByUserIdxAndCalendarDate(Long userIdx, Date calendarDate);

    Page<AppCalendar> findByUserIdxOrderByCalendarDateDesc(Long userIdx, Pageable pageable);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query(value = """
           MERGE INTO app_calendar t
           USING (SELECT :userIdx AS user_idx, :calendarDate AS calendar_date FROM dual) s
              ON (t.user_idx = s.user_idx AND t.calendar_date = s.calendar_date)
           WHEN MATCHED THEN
             UPDATE SET t.check_in_yn = 1,
                        t.check_in_at = SYSTIMESTAMP,
                        t.updated_at = SYSTIMESTAMP
               WHERE t.check_in_yn = 0
           WHEN NOT MATCHED THEN
             INSERT (user_idx, calendar_date, check_in_yn, check_in_at, created_at, updated_at)
             VALUES (:userIdx, :calendarDate, 1, SYSTIMESTAMP, SYSTIMESTAMP, SYSTIMESTAMP)
           """, nativeQuery = true)
    int checkInIfAbsentMerge(@Param("userIdx") Long userIdx, @Param("calendarDate") Date calendarDate);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("""
           UPDATE AppCalendar c
              SET c.checkInYn = 1,
                  c.checkInAt = CURRENT_TIMESTAMP,
                  c.updatedAt = CURRENT_TIMESTAMP
            WHERE c.userIdx = :userIdx
              AND c.calendarDate = :calendarDate
              AND c.checkInYn = 0
           """)
    int markCheckedIfNotYet(@Param("userIdx") Long userIdx, @Param("calendarDate") Date calendarDate);

    @Query(value = """
           SELECT COUNT(*)
             FROM app_calendar c
            WHERE c.user_idx = :userIdx
              AND c.check_in_yn = 1
              AND TO_CHAR(c.calendar_date, 'YYYY-MM') = :monthKey
           """, nativeQuery = true)
    int countAttendanceDaysInMonth(@Param("userIdx") Long userIdx, @Param("monthKey") String monthKey);
}
