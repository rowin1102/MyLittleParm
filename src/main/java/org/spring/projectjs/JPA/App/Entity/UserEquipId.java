package org.spring.projectjs.JPA.App.Entity;

import jakarta.persistence.Embeddable;
import lombok.*;

import java.io.Serializable;

@Embeddable
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @EqualsAndHashCode
public class UserEquipId {
    private Long userIdx;
    private String slot;
    private Long itemId;
}
