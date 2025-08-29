package org.spring.projectjs.jdbc;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

@Data
public class CalendarDTO {
   
   private long calendar_idx;
   private String member_id;
   private String plants_name;
   
   @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private java.sql.Date start_date;
   
   private String title;
   private String memo;
   private java.sql.Date reg_date; // 일정 등록 시점
   private java.sql.Date upd_date; // 일정 수정 시점
   private String color;
   
   @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
   private java.sql.Date end_date;
   
}
