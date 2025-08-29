package org.spring.projectjs.JPA.App.Entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "furniture")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Furniture {
    @Id
    @Column(name = "furn_id")
    private Long furnId;

    @Column(name = "furn_name", length = 50)
    private String furnName;

    @Column(name = "description", length = 200)
    private String description;
}