package org.spring.projectjs.jdbc;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface ICalendar {
	List<PlantDTO> searchPlantName(@Param("keyword") String keyword);

   int insertCalendar(CalendarDTO calendarDTO);

   List<CalendarDTO> listCalendarByRange(@Param("member_id") String memberId, @Param("start") java.sql.Date start,
         @Param("end") java.sql.Date end);

   int updateCalendar(@Param("id") Long id, @Param("title") String title, @Param("memo") String memo,
         @Param("memberId") String memberId);

   int deleteCalendar(@Param("id") Long id, @Param("memberId") String memberId);

   int updateCalendarDate(@Param("id") Long id, @Param("startDate") java.sql.Date startDate,
         @Param("memberId") String memberId);
}
