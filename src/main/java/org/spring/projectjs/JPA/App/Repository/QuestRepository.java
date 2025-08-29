// src/main/java/org/spring/projectjs/JPA/App/Repository/QuestRepository.java
package org.spring.projectjs.JPA.App.Repository;

import org.spring.projectjs.JPA.App.Entity.Quest;
import org.spring.projectjs.JPA.App.Entity.QuestType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface QuestRepository extends JpaRepository<Quest, Long> {
    List<Quest> findAllByActiveTrue();
    Optional<Quest> findByQuestTypeAndActive(QuestType questType, Boolean active);
}
