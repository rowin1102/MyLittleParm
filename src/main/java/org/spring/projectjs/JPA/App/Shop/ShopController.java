// src/main/java/org/spring/projectjs/JPA/App/Shop/ShopController.java
package org.spring.projectjs.JPA.App.Shop;

import lombok.RequiredArgsConstructor;
import org.spring.projectjs.JPA.App.Entity.PurchaseResult;
import org.spring.projectjs.JPA.App.Entity.ShopItem;
import org.spring.projectjs.JPA.App.Entity.UserFurniture;
import org.spring.projectjs.JPA.App.Entity.UserPurchase;
import org.spring.projectjs.auth.JwtTokenProvider;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/shop")
@CrossOrigin(origins = "*", allowedHeaders = "*", methods = {
        RequestMethod.GET, RequestMethod.POST, RequestMethod.OPTIONS
})
public class ShopController {

    private final ShopService shopService;
    private final JwtTokenProvider jwtTokenProvider; // 토큰 → userIdx 추출

    /** Authorization 헤더에서 Bearer 토큰 추출 */
    private String resolveToken(String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "No Bearer token");
        }
        return authHeader.substring(7);
    }

    /** 토큰에서 userIdx 꺼내기 (null 방지) */
    private Long getUserIdx(String authHeader) {
        String token = resolveToken(authHeader);
        Long uid = jwtTokenProvider.getUserIdx(token);
        if (uid == null) throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid token");
        return uid;
    }

    /** 테마별 아이템 조회 */
    @GetMapping("/items/thema/{thema}")
    public ResponseEntity<List<ShopItem>> getItemsByThema(
            @RequestHeader("Authorization") String authHeader,
            @PathVariable String thema) {
        // 토큰 검증만 통과시키고 사용은 안 함
        resolveToken(authHeader);
        System.out.println("[SHOP] items by thema=" + thema);
        return ResponseEntity.ok(shopService.getItemsByThema(thema));
    }

    /** 내 구매 목록 */
    @GetMapping("/purchases")
    public ResponseEntity<List<UserPurchase>> getMyPurchases(
            @RequestHeader("Authorization") String authHeader) {
        Long userIdx = getUserIdx(authHeader);
        System.out.println("[SHOP] getMyPurchases userIdx=" + userIdx);
        return ResponseEntity.ok(shopService.getMyPurchases(userIdx));
    }

    /** 아이템 구매 */
    @PostMapping("/purchase/{itemId}")
    public ResponseEntity<PurchaseResult> purchaseItem(
            @RequestHeader("Authorization") String authHeader,
            @PathVariable Long itemId) {
        Long userIdx = getUserIdx(authHeader);
        System.out.println("[SHOP] HIT purchase userIdx=" + userIdx + ", itemId=" + itemId);
        PurchaseResult result = shopService.purchaseItem(userIdx, itemId);
        System.out.println("[SHOP] RESULT success=" + result.isSuccess() + ", msg=" + result.getMessage());
        return ResponseEntity.ok(result);
    }

    /** 내 가구 목록 */
    @GetMapping("/furnitures")
    public ResponseEntity<List<UserFurniture>> getMyFurnitures(
            @RequestHeader("Authorization") String authHeader) {
        Long userIdx = getUserIdx(authHeader);
        System.out.println("[SHOP] getMyFurnitures userIdx=" + userIdx);
        return ResponseEntity.ok(shopService.getMyFurnitures(userIdx));
    }

    /** 가구 획득 */
    @PostMapping("/furnitures/{furnId}")
    public ResponseEntity<Boolean> acquireFurniture(
            @RequestHeader("Authorization") String authHeader,
            @PathVariable Long furnId) {
        Long userIdx = getUserIdx(authHeader);
        System.out.println("[SHOP] acquireFurniture userIdx=" + userIdx + ", furnId=" + furnId);
        return ResponseEntity.ok(shopService.acquireFurniture(userIdx, furnId));
    }

    /** CORS 프리플라이트(선택) */
    @RequestMapping(value = {"/purchase/{itemId}", "/purchases", "/furnitures", "/items/thema/{thema}"}, method = RequestMethod.OPTIONS)
    public ResponseEntity<Void> corsPreflight() {
        return ResponseEntity.ok().build();
    }
}
