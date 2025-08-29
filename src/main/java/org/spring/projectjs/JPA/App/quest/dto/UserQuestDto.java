package org.spring.projectjs.JPA.App.quest.dto;

public record UserQuestDto(
        Long id,
        String status,   // 진행 상태 등
        QuestDto quest
) {}