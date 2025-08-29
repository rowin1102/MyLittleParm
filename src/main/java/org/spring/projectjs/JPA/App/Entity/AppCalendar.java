package org.spring.projectjs.JPA.App.Entity;

import jakarta.persistence.*;
import lombok.*;
import java.util.Date;

@Entity
@Table(name = "app_calendar")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class AppCalendar {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "calendar_id")
    private Long calendarId;

    @Column(name = "user_idx", nullable = false)
    private Long userIdx;

    @Temporal(TemporalType.DATE)
    @Column(name = "calendar_date", nullable = false)
    private Date calendarDate;                 // 일 단위 키 (예: 2025-08-22)

    @Column(name = "check_in_yn", nullable = false)
    private Integer checkInYn = 0;            // 0/1

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "check_in_at")
    private Date checkInAt;                   // 출석 시각

    @Column(name = "note", length = 255)
    private String note;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "created_at", nullable = false)
    private Date createdAt;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "updated_at", nullable = false)
    private Date updatedAt;

    @PrePersist
    protected void onCreate() {
        Date now = new Date();
        if (this.createdAt == null) this.createdAt = now;
        if (this.updatedAt == null) this.updatedAt = now;
        if (this.checkInYn == null) this.checkInYn = 0;
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = new Date();
    }
}
