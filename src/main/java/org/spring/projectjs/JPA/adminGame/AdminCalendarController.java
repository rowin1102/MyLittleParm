package org.spring.projectjs.JPA.adminGame;

import lombok.RequiredArgsConstructor;
import org.spring.projectjs.JPA.App.Entity.AppCalendar;
import org.spring.projectjs.JPA.App.Repository.AppCalendarRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

/** 출석(캘린더) 관리자 */
@RestController
@RequestMapping("/api/admin/calendar")
@RequiredArgsConstructor
public class AdminCalendarController {

    private final AppCalendarRepository calendarRepository;

    /** 단건 조회 */
    @GetMapping("/{id}")
    public ResponseEntity<AppCalendar> getCalendar(@PathVariable Long id) {
        return calendarRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /** 출석/메모 부분 수정 (null 값은 무시) */
    @PutMapping("/{id}")
    public ResponseEntity<AppCalendar> updateCalendar(
            @PathVariable Long id,
            @RequestBody CalendarUpdateReq req) {

        Optional<AppCalendar> opt = calendarRepository.findById(id);
        if (opt.isEmpty()) return ResponseEntity.notFound().build();

        AppCalendar c = opt.get();
        if (req.checkInYn() != null) c.setCheckInYn(req.checkInYn());
        if (req.note() != null)      c.setNote(req.note());

        return ResponseEntity.ok(calendarRepository.save(c));
    }

    /** 요청 DTO: null 은 기존 값 유지 */
    public record CalendarUpdateReq(Integer checkInYn, String note) {}
}
