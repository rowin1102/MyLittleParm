package org.spring.projectjs.JPA.App.Decor.dto;

import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class EquippedItemDto {
    private String slot;
    private Long itemId;
    private String url;
}