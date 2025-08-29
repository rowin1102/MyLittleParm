// src/main/java/org/spring/projectjs/JPA/App/plants/PlantController.java
package org.spring.projectjs.JPA.adminPlants;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/plants")
public class PlantController {

    private final PlantService service;

    /** 목록 조회 + 검색/필터 + 페이징 + 정렬 */
    @GetMapping
    public ResponseEntity<Page<Plant>> list(
            @RequestParam(value = "q", required = false) String q,
            @RequestParam(value = "difficulty", required = false) String difficulty,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "20") int size,
            @RequestParam(value = "sort", required = false) String sort
    ) {
        return ResponseEntity.ok(service.list(q, difficulty, page, size, sort));
    }

    /** 단건 조회 */
    @GetMapping("/{id}")
    public ResponseEntity<Plant> get(@PathVariable("id") Long id) {
        return ResponseEntity.ok(service.get(id));
    }

    /** 생성 */
    @PostMapping
    public ResponseEntity<Plant> create(@RequestBody PlantReq req) {
        return ResponseEntity.ok(service.create(req.toEntity()));
    }

    /** 수정(전체 업데이트) */
    @PutMapping("/{id}")
    public ResponseEntity<Plant> update(@PathVariable("id") Long id, @RequestBody PlantReq req) {
        return ResponseEntity.ok(service.update(id, req.toEntity()));
    }

    /** 삭제 */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable("id") Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }

    @Data
    public static class PlantReq {
        private String name;
        private String englishName;
        private String difficulty;   // 1글자
        private java.math.BigDecimal minTemp;
        private java.math.BigDecimal maxTemp;
        private Integer minGrowDays;
        private Integer maxGrowDays;

        Plant toEntity() {
            return Plant.builder()
                    .name(name)
                    .englishName(englishName)
                    .difficulty(difficulty)
                    .minTemp(minTemp)
                    .maxTemp(maxTemp)
                    .minGrowDays(minGrowDays)
                    .maxGrowDays(maxGrowDays)
                    .build();
        }
    }
}
