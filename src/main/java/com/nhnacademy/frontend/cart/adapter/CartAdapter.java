package com.nhnacademy.frontend.cart.adapter;

import com.nhnacademy.frontend.cart.domain.OwnerType;
import com.nhnacademy.frontend.cart.dto.request.CartAddItemRequest;
import com.nhnacademy.frontend.cart.dto.request.CartUpdateRequest;
import com.nhnacademy.frontend.cart.dto.response.CartCreateResponse;
import com.nhnacademy.frontend.cart.dto.response.CartResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@FeignClient(name = "gateway-service", contextId = "cartAdapter")
public interface CartAdapter {
    @PostMapping("/user-api/carts/me")
    CartCreateResponse createCart(@RequestHeader("X-OWNER-TYPE") OwnerType ownerType);

    @GetMapping("/user-api/carts/me")
    CartResponse getCart(@RequestHeader("X-OWNER-TYPE") OwnerType ownerType,
                         @RequestHeader(value = "X-GUEST-UUID", required = false) String guestUUID);

    @PostMapping("/user-api/carts/me/items")
    CartResponse addItemToCart(@RequestHeader("X-OWNER-TYPE") OwnerType ownerType,
                               @RequestHeader(value = "X-GUEST-UUID", required = false) String guestUUID,
                               @RequestBody CartAddItemRequest request);

    @PatchMapping("/user-api/carts/me/items/{itemId}")
    CartResponse updateItemQuantity(@RequestHeader("X-OWNER-TYPE") OwnerType ownerType,
                                    @RequestHeader(value = "X-GUEST-UUID", required = false) String guestUUID,
                                    @PathVariable String itemId, @RequestBody CartUpdateRequest request);

    @DeleteMapping("/user-api/carts/me/items/{itemId}")
    CartResponse deleteItemFromCart(@RequestHeader("X-OWNER-TYPE") OwnerType ownerType,
                                    @RequestHeader(value = "X-GUEST-UUID", required = false) String guestUUID,
                                    @PathVariable String itemId);

    @DeleteMapping("/user-api/carts/me/items")
    CartResponse deleteItemsFromCart(@RequestHeader("X-OWNER-TYPE") OwnerType ownerType,
                                        @RequestHeader(value = "X-GUEST-UUID", required = false) String guestUUID,
                                        @RequestBody List<Long> itemIds);

}
