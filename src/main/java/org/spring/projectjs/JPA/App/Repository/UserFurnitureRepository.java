package org.spring.projectjs.JPA.App.Repository;

import org.spring.projectjs.JPA.App.Entity.UserFurniture;
import org.spring.projectjs.JPA.App.Entity.UserFurnitureId;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserFurnitureRepository extends JpaRepository<UserFurniture, UserFurnitureId> {

    // 유저가 보유한 가구들
    List<UserFurniture> findByUserIdx(Long userIdx);

    boolean existsByUserIdxAndFurnId(Long userIdx, Long furnId);
}
