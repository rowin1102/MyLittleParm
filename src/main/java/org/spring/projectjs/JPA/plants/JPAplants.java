package org.spring.projectjs.JPA.plants;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;


@NoArgsConstructor
@AllArgsConstructor
@ToString
@Getter
@Setter
public class JPAplants {

    
    private Long id;

    private String name;
    private String series;
    private String englishName;

    // 난이도 (1 ~ 5)
    private Integer difficulty;

    private Integer minTemp;

    private Integer maxTemp;

    private Integer minGrowDays;

    private Integer maxGrowDays;
    
    private Integer minHumidity;
    
    private Integer maxHumidity;
    
    private Integer amountLight;

}
