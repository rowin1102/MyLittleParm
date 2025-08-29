package org.spring.projectjs.JPA.App.Repository;

import org.spring.projectjs.JPA.App.Entity.Furniture;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FurnitureRepository extends JpaRepository<Furniture, Long> {
}
