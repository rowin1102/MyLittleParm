package org.spring.projectjs.jdbc;

import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CalendarService {
   
   @Autowired
   private ICalendar iCalendar;
   
   public List<PlantDTO> search(String keyword) {
      return iCalendar.searchPlantName(keyword);
   }
   
   @Transactional
    public int insertCalendar(CalendarDTO calendarDTO) {
        return iCalendar.insertCalendar(calendarDTO);
    }
   
   public List<CalendarDTO> list(String memberId, java.sql.Date startYmd, java.sql.Date endYmd) {
        return iCalendar.listCalendarByRange(memberId, startYmd, endYmd);
    }
   
   public int update(Long id, String title, String memo, String memberId){
       return iCalendar.updateCalendar(id, title, memo, memberId);
   }
   public int delete(Long id, String memberId){
       return iCalendar.deleteCalendar(id, memberId);
   }
   
   @Transactional
   public int updateCalendarDate(Long id, java.sql.Date startDate, String memberId) {
       return iCalendar.updateCalendarDate(id, startDate, memberId);
   }
}
