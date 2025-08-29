package org.spring.projectjs.JPA.App.Entity;

import java.io.Serializable;
import java.util.Date;
import java.util.Objects;

public class UserPurchaseId implements Serializable {
    private Long userIdx;       // 엔티티와 동일
    private Long itemId;        // 엔티티와 동일
    private Date purchaseDate;  // 엔티티와 동일

    public UserPurchaseId() {}

    public UserPurchaseId(Long userIdx, Long itemId, Date purchaseDate) {
        this.userIdx = userIdx;
        this.itemId = itemId;
        this.purchaseDate = purchaseDate;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof UserPurchaseId that)) return false;
        return Objects.equals(userIdx, that.userIdx) &&
                Objects.equals(itemId, that.itemId) &&
                Objects.equals(purchaseDate, that.purchaseDate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userIdx, itemId, purchaseDate);
    }
}