package org.spring.projectjs.JPA.App.Decor.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InventoryItemDto {

    private Long itemId;
    private String itemName;
    private String description;
    private Integer priceGold;
    private String slot;     // = thema
    private String url;
    private boolean equipped;
}
