package org.spring.projectjs.JPA.App.Entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.Date;

@Entity
@Table(name = "attendance_logs")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class AttendanceLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "log_id")
    private Long logId;

    @Column(name = "user_idx", nullable = false)
    private Long userIdx;                         // FK 미설정

    @Column(name = "window_key", nullable = false, length = 16)
    private String windowKey;                    // 'YYYY-MM-DD'

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "checked_at", nullable = false)
    private Date checkedAt;                      // 체크 시각

    @Column(name = "source", nullable = false, length = 30)
    private String source = "manual";            // 'manual' | 'api' | ...

    @Column(name = "memo", length = 200)
    private String memo;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "created_at", nullable = false)
    private Date createdAt;

    @PrePersist
    protected void onCreate() {
        Date now = new Date();
        if (this.checkedAt == null) this.checkedAt = now;
        if (this.createdAt == null) this.createdAt = now;
        if (this.source == null) this.source = "manual";
    }
}
