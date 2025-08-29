package org.spring.projectjs.JPA.App.Entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.*;
import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Embeddable
public class EUserQuestId implements Serializable {
    @Column(name = "user_idx")
    private Long userIdx;

    @Column(name = "quest_id")
    private Long questId;
}
