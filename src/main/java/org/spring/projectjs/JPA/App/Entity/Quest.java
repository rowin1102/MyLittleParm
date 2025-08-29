package org.spring.projectjs.JPA.App.Entity;

import jakarta.persistence.*;
import lombok.*;
import java.util.Date;

@Entity
@Table(name = "quest")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Quest {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "quest_seq")
    @SequenceGenerator(name = "quest_seq", sequenceName = "seq_quest_num", allocationSize = 1)

    @Column(name = "quest_id")
    private Long questId;

    @Enumerated(EnumType.STRING)
    @Column(name = "quest_type", length = 50, nullable = false)
    private QuestType questType;              // DAILY_ATTENDANCE | DAILY_PHOTO

    @Column(name = "name", length = 100)
    private String name;

    @Column(name = "description", length = 500)
    private String description;

    @Lob
    @Column(name = "rule_json")
    private String ruleJson;

    @Column(name = "reward_gold", nullable = false)
    private Long rewardGold = 0L;

    @Column(name = "reward_exp", nullable = false)
    private Long rewardExp = 0L;

    @Column(name = "active", nullable = false)
    private Boolean active = true;

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
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = new Date();
    }
}
