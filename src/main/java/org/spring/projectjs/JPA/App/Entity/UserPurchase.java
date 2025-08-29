package org.spring.projectjs.JPA.App.Entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Entity
@Table(name = "user_purchase")
@IdClass(UserPurchaseId.class)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserPurchase {
    @Id
    @Column(name = "user_idx")
    private Long userIdx;

    @Id
    @Column(name = "item_id")
    private Long itemId;

    @Id
    @Column(name = "purchase_date")
    private Date purchaseDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item_id", insertable = false, updatable = false)
    @com.fasterxml.jackson.annotation.JsonIgnore
    private ShopItem shopItem;

    public UserPurchase(Long userIdx, Long itemId, Date date) {
        this.userIdx = userIdx;
        this.itemId = itemId;
        this.purchaseDate = date;
    }
}
