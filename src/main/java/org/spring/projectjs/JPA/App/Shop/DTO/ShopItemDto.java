package org.spring.projectjs.JPA.App.Shop.DTO;

import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class ShopItemDto {
    private Long itemId;
    private String itemName;
    private Integer priceGold;
    private String thema;
    private String url;
}