package com.nhnacademy.frontend.cart.service;

import com.nhnacademy.frontend.cart.dto.CartOperationResult;
import com.nhnacademy.frontend.cart.dto.CartViewResponse;

import java.util.List;
import java.util.Map;

public interface CartService {
    CartViewResponse getCartItems(boolean isLoggedIn, String guestUUID);

    CartOperationResult addToCart(Long bookId, int quantity, boolean isLoggedIn, String guestUUID);

    CartOperationResult deleteCartItems(List<Long> bookIds, boolean isLoggedIn, String guestUUID);

    CartOperationResult updateCartItems(Map<Long, Integer> quantities, boolean isLoggedIn, String guestUUID);
}
