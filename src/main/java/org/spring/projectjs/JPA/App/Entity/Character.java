// src/main/java/.../Entity/CharacterEntity.java
package org.spring.projectjs.JPA.App.Entity;

import jakarta.persistence.*;
import lombok.*;
import java.util.Date;

@Entity
@Table(name = "characters",
        uniqueConstraints = @UniqueConstraint(name = "uk_char_user", columnNames = "user_idx"))
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
@SequenceGenerator(name = "seq_charcters_num", sequenceName = "seq_charcters_num", allocationSize = 1)
public class Character {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq_charcters_num")
    @Column(name = "char_id")
    private Long charId;

    @Column(name = "user_idx", nullable = false)
    private Long userIdx;

    @Column(name = "char_name", length = 50)
    private String charName;

    @Column(name = "stage", nullable = false)
    private Integer stage = 1; // 1~4

    @Column(name = "char_face", length = 100)
    private String charFace;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "created_at", nullable = false)
    private Date createdAt;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "updated_at", nullable = false)
    private Date updatedAt;

    @PrePersist
    void onCreate() {
        Date now = new Date();
        if (createdAt == null) createdAt = now;
        if (updatedAt == null) updatedAt = now;
        if (stage == null) stage = 1;
    }

    @PreUpdate
    void onUpdate() {
        updatedAt = new Date();
    }
}
