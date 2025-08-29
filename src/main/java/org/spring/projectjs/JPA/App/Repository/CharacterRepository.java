// src/main/java/.../Repository/CharacterRepository.java
package org.spring.projectjs.JPA.App.Repository;

import org.spring.projectjs.JPA.App.Entity.Character;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CharacterRepository extends JpaRepository<Character, Long> {
    Optional<Character> findByUserIdx(Long userIdx);
    boolean existsByUserIdx(Long userIdx);
}
