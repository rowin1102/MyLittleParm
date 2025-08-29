// src/main/java/org/spring/projectjs/JPA/App/plants/Plant.java
package org.spring.projectjs.JPA.adminPlants;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor @Builder
@Entity
@Table(name = "PLANTS")
public class Plant {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // ORA 12c+ IDENTITY 호환
    @Column(name = "PLANTS_IDX")
    private Long plantsIdx;

    @Column(name = "NAME", nullable = false, length = 30)
    private String name;

    @Column(name = "ENGLISH_NAME", nullable = false, length = 30)
    private String englishName;

    @Column(name = "DIFFICULTY", nullable = false, length = 1)
    private String difficulty; // '1','2','3' 등 1글자

    @Column(name = "MIN_TEMP", nullable = false, precision = 3, scale = 1)
    private BigDecimal minTemp;

    @Column(name = "MAX_TEMP", nullable = false, precision = 3, scale = 1)
    private BigDecimal maxTemp;

    @Column(name = "MIN_GROW_DAYS", nullable = false)
    private Integer minGrowDays;

    @Column(name = "MAX_GROW_DAYS", nullable = false)
    private Integer maxGrowDays;

    @PrePersist @PreUpdate
    void validateRanges() {
        if (minTemp != null && maxTemp != null && minTemp.compareTo(maxTemp) > 0) {
            throw new IllegalArgumentException("min_temp must be <= max_temp");
        }
        if (minGrowDays != null && maxGrowDays != null && minGrowDays > maxGrowDays) {
            throw new IllegalArgumentException("min_grow_days must be <= max_grow_days");
        }
        if (difficulty != null && difficulty.trim().length() != 1) {
            throw new IllegalArgumentException("difficulty must be 1 char");
        }
    }
}
