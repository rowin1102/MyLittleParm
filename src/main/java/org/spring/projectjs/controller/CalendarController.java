package org.spring.projectjs.controller;

import org.spring.projectjs.jdbc.CalendarDTO;
import org.spring.projectjs.jdbc.CalendarService;
import org.spring.projectjs.jdbc.PlantDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
public class CalendarController {

   @Autowired
   private CalendarService calendarService;

   @GetMapping("/calendar.do")
   public String calendar() {
      return "calendar/calendar";
   }

   @GetMapping(value = "/search.do", produces = "application/json; charset=UTF-8")
   @ResponseBody
   public List<PlantDTO> search(@RequestParam("keyword") String keyword) {
      return calendarService.search(keyword);
   }

   @GetMapping(value = "/calendar/list.do", produces = "application/json; charset=UTF-8")
   @ResponseBody
   public List<Map<String, Object>> list(@RequestParam("start") String start, @RequestParam("end") String end, Principal principal) {
      if (principal == null)
         return Collections.emptyList();

      java.sql.Date s = java.sql.Date.valueOf(start);
      java.sql.Date e = java.sql.Date.valueOf(end);

      List<CalendarDTO> rows = calendarService.list(principal.getName(), s, e);

      return rows.stream().map(r -> {
         Map<String, Object> m = new HashMap<>();
         m.put("id", r.getCalendar_idx());
         m.put("title", r.getTitle());
         m.put("memo", r.getMemo());
         m.put("start", r.getStart_date().toString());
         m.put("end", r.getEnd_date().toString());
         m.put("color", r.getColor());
         m.put("plant", r.getPlants_name());
         return m;
      }).collect(Collectors.toList());
   }

   @PostMapping(value = "/calendar/save.do", consumes = "application/json", produces = "application/json; charset=UTF-8")
   @ResponseBody
   public Map<String, Object> save(@RequestBody CalendarDTO dto, Principal principal) {
      if (principal == null)
         return Map.of("ok", false, "msg", "unauthorized");

      dto.setMember_id(principal.getName());

      int rows = calendarService.insertCalendar(dto);
      return Map.of("ok", rows > 0, "id", dto.getCalendar_idx());
   }

   @PostMapping(value = "/calendar/update.do", produces = "application/json; charset=UTF-8")
    @ResponseBody
    public Map<String, Object> update(@RequestBody Map<String, String> body, Principal principal) {
        if (principal == null) return Map.of("ok", false, "msg", "unauthorized");
        Long id = parseId(body.get("id"));
        if (id == null) return Map.of("ok", false, "msg", "invalid id");

        String title = body.get("title");
        String memo = body.get("memo");
        int updated = calendarService.update(id, title, memo, principal.getName());
        return Map.of("ok", updated > 0);
    }
   
   @PostMapping(value = "/calendar/delete.do", produces = "application/json; charset=UTF-8")
    @ResponseBody
    public Map<String, Object> delete(@RequestBody Map<String, String> body, Principal principal) {
        if (principal == null) return Map.of("ok", false, "msg", "unauthorized");
        Long id = parseId(body.get("id"));
        if (id == null) return Map.of("ok", false, "msg", "invalid id");

        int deleted = calendarService.delete(id, principal.getName());
        return Map.of("ok", deleted > 0);
    }
   
   @PostMapping(value = "/calendar/updateDate.do", produces = "application/json; charset=UTF-8")
    @ResponseBody
    public Map<String, Object> updateDate(@RequestBody Map<String, String> body, Principal principal) {
        if (principal == null) return Map.of("ok", false, "msg", "unauthorized");
        try {
            Long id = parseId(body.get("id"));
            if (id == null) return Map.of("ok", false, "msg", "invalid id");

            java.sql.Date startDate = java.sql.Date.valueOf(body.get("start_date"));
            int updated = calendarService.updateCalendarDate(id, startDate, principal.getName());
            return Map.of("ok", updated > 0);
        } catch (Exception e) {
            return Map.of("ok", false, "msg", "invalid request");
        }
    }
   
   private Long parseId(String s) {
        if (s == null) return null;
        try {
            return Long.parseLong(s.trim());
        } catch (NumberFormatException e) {
            return null;
        }
    }
}
