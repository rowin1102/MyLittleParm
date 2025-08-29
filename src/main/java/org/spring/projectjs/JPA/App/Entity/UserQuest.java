package org.spring.projectjs.JPA.App.Entity;

import jakarta.persistence.*;
import lombok.*;
import java.util.Date;

@Entity
@Table(name = "user_quest")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class UserQuest {

    @EmbeddedId
    private EUserQuestId id; // (userIdx, questId)

    // FK 제약조건 생성 방지
    @ManyToOne(fetch = FetchType.LAZY) @MapsId("userIdx")
    @JoinColumn(
            name = "user_idx",
            referencedColumnName = "member_idx",
            foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT)
    )
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY) @MapsId("questId")
    @JoinColumn(
            name = "quest_id",
            referencedColumnName = "quest_id",
            foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT)
    )
    private Quest quest;

    @Column(name = "status", nullable = false, length = 1)
    private String status = "n"; // n: 미완, y: 완료

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "completed_at")
    private Date completedAt;

    @Column(name = "window_key", length = 16) // 예: "2025-08-22"
    private String windowKey;

    @Column(name = "progress_count")
    private Integer progressCount = 0;

    @Lob
    @Column(name = "progress_json")
    private String progressJson;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "created_at", nullable = false)
    private Date createdAt;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "updated_at", nullable = false)
    private Date updatedAt;

    @PrePersist
    protected void onCreate() {
        Date now = new Date();
        this.createdAt = now;
        this.updatedAt = now;
        if (this.status == null) this.status = "n";
        if (this.progressCount == null) this.progressCount = 0;
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = new Date();
    }
}
