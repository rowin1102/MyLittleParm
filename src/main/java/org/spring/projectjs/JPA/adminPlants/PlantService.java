// src/main/java/org/spring/projectjs/JPA/App/plants/PlantService.java
package org.spring.projectjs.JPA.adminPlants;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PlantService {

    private final PlantRepository repo;

    @Transactional(readOnly = true)
    public Page<Plant> list(String q, String difficulty, int page, int size, String sort) {
        Sort s = Sort.by(Sort.Direction.ASC, "plantsIdx");
        if (sort != null && !sort.isBlank()) {
            // 예: sort = "name,asc" 또는 "maxTemp,desc"
            String[] parts = sort.split(",");
            String prop = parts[0].trim();
            Sort.Direction dir = (parts.length > 1 && "desc".equalsIgnoreCase(parts[1])) ? Sort.Direction.DESC : Sort.Direction.ASC;
            s = Sort.by(dir, prop);
        }
        Pageable pageable = PageRequest.of(Math.max(page,0), Math.max(size,1), s);
        String qNorm = (q == null || q.isBlank()) ? null : q.trim();
        String dNorm = (difficulty == null || difficulty.isBlank()) ? null : difficulty.trim();
        return repo.search(qNorm, dNorm, pageable);
    }

    @Transactional(readOnly = true)
    public Plant get(Long id) {
        return repo.findById(id).orElseThrow(() -> new IllegalArgumentException("not found: " + id));
    }

    @Transactional
    public Plant create(Plant in) {
        in.setPlantsIdx(null); // 방지
        return repo.save(in);
    }

    @Transactional
    public Plant update(Long id, Plant in) {
        Plant p = get(id);
        p.setName(in.getName());
        p.setEnglishName(in.getEnglishName());
        p.setDifficulty(in.getDifficulty());
        p.setMinTemp(in.getMinTemp());
        p.setMaxTemp(in.getMaxTemp());
        p.setMinGrowDays(in.getMinGrowDays());
        p.setMaxGrowDays(in.getMaxGrowDays());
        return p; // JPA dirty checking
    }

    @Transactional
    public void delete(Long id) {
        repo.deleteById(id);
    }
}
