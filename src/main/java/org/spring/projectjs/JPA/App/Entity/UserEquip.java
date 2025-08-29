package org.spring.projectjs.JPA.App.Entity;

import jakarta.persistence.*;
import lombok.*;

import java.sql.Timestamp;
import java.util.Date;

@Entity
@Table(name = "user_equip")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class UserEquip {

    @EmbeddedId
    private UserEquipId id;

    @Column(name = "equipped_at")
    private Timestamp equippedAt;

    // 편의 접근자
    public Long getUserIdx() { return id.getUserIdx(); }
    public String getSlot()  { return id.getSlot(); }
    public Long getItemId()  { return id.getItemId(); }
}