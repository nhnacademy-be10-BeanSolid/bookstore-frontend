package com.nhnacademy.frontend.cart.service;

import com.nhnacademy.frontend.cart.dto.CartOperationResult;
import com.nhnacademy.frontend.cart.dto.CartViewResponse;
import com.nhnacademy.frontend.cart.dto.request.CartItemUpdateRequest;

import java.util.List;

public interface CartService {
    CartViewResponse getCartItems(boolean isLoggedIn, String guestUUID);

    CartOperationResult addToCart(Long bookId, int quantity, boolean isLoggedIn, String guestUUID);

    CartOperationResult deleteCartItems(List<Long> bookIds, boolean isLoggedIn, String guestUUID);

    CartOperationResult updateCartItems(List<CartItemUpdateRequest> request, boolean isLoggedIn, String guestUUID);
}
