package org.spring.projectjs.JPA.App.Entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "user_furniture")
@IdClass(UserFurnitureId.class)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserFurniture {
    @Id
    @Column(name = "user_idx")
    private Long userIdx;

    @Id
    @Column(name = "furn_id")
    private Long furnId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "furn_id", insertable = false, updatable = false)
    private Furniture furniture;

    public UserFurniture(Long userIdx, Long furnId) {
    }
}
