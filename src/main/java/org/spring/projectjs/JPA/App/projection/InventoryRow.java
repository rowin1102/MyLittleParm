package org.spring.projectjs.JPA.App.projection;

public interface InventoryRow {
    Long getItemId();
    String getItemName();
    String getDescription();
    Integer getPriceGold();
    String getSlot();     // = thema
    String getUrl();
    String getEquipped(); // 'Y' or 'N'
}