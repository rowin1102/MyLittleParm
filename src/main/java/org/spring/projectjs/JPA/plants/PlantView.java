package org.spring.projectjs.JPA.plants;

import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @ToString
public class PlantView {
 private String name;          // 한글명 (CSV: 식물명)
 private String englishName;   // 영문명
 private String series;        // 분류(잎채소/뿌리채소/허브/꽃/관엽/꽃/과일/과채류/허브/꽃 등)
 private Integer difficulty;   // grade
 private Integer minTemp;
 private Integer maxTemp;
 private Integer amountLight;  // 선택
 private Integer minGrowDays;
 private Integer maxGrowDays;
 private Integer minHumidity;  // 선택
 private Integer maxHumidity;  // 선택
}
