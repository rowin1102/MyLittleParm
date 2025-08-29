package org.spring.projectjs.JPA.App.Shop.mapper;

import org.spring.projectjs.JPA.App.Entity.ShopItem;
import org.spring.projectjs.JPA.App.Entity.UserPurchase;
import org.spring.projectjs.JPA.App.Shop.DTO.ShopItemDto;
import org.spring.projectjs.JPA.App.Shop.DTO.UserPurchaseDto;

import java.util.List;
import java.util.stream.Collectors;

public final class ShopMapper {
    private ShopMapper() {}

    public static ShopItemDto toDto(ShopItem e) {
        if (e == null) return null;
        return ShopItemDto.builder()
                .itemId(e.getItemId())
                .itemName(e.getItemName())
                .priceGold(e.getPriceGold())
                .thema(e.getThema())
                .url(e.getUrl())
                .build();
    }

    public static List<ShopItemDto> toShopItemDtos(List<ShopItem> list) {
        return list.stream().map(ShopMapper::toDto).collect(Collectors.toList());
    }

    public static UserPurchaseDto toDto(UserPurchase e) {
        if (e == null) return null;
        return UserPurchaseDto.builder()
                .itemId(e.getItemId())
                .purchasedAt(e.getPurchaseDate())
                .build();
    }

    public static List<UserPurchaseDto> toUserPurchaseDtos(List<UserPurchase> list) {
        return list.stream().map(ShopMapper::toDto).collect(Collectors.toList());
    }
}