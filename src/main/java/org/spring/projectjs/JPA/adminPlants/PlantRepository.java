// src/main/java/org/spring/projectjs/JPA/App/plants/PlantRepository.java
package org.spring.projectjs.JPA.adminPlants;

import org.springframework.data.domain.*;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;

public interface PlantRepository extends JpaRepository<Plant, Long> {

    @Query("""
        select p from Plant p
         where (:q is null or lower(p.name) like lower(concat('%', :q, '%'))
                         or lower(p.englishName) like lower(concat('%', :q, '%')))
           and (:diff is null or p.difficulty = :diff)
    """)
    Page<Plant> search(
            @Param("q") String q,
            @Param("diff") String difficulty,
            Pageable pageable
    );
}
