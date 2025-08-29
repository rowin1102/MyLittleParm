package org.spring.projectjs.JPA.App.quest.dto;

public record QuestDto(
        Long id,
        String title,
        String description,
        Integer reward
) {}
