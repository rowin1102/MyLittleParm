package org.spring.projectjs.JPA.App.Entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "shop_items")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ShopItem {
    @Id
    @Column(name = "item_id")
    private Long itemId;

    @Column(name = "item_name", length = 50)
    private String itemName;

    @Column(name = "description", length = 200)
    private String description;

    @Column(name = "price_gold")
    @Builder.Default
    private Integer priceGold = 0;

    @Column(name = "thema", length = 50)
    private String thema;

    @Column(name = "url", length = 30)
    private  String url;
}

