package com.nhnacademy.frontend.cart.service;

import com.nhnacademy.frontend.cart.dto.view.CartItemViewModel;
import jakarta.servlet.http.HttpServletResponse;

import java.util.List;

public interface CartService {
    List<CartItemViewModel> getCartItems(boolean isLoggedIn,
                                         String guestUUID,
                                         HttpServletResponse response);

    void addToCart(Long bookId, int quantity, boolean isLoggedIn, String guestUUID, HttpServletResponse response);

    void deleteCartItems(List<Long> bookIds, boolean isLoggedIn, String guestUUID, HttpServletResponse response);

    void updateCartItems(java.util.Map<Long, Integer> quantities, boolean isLoggedIn, String guestUUID, HttpServletResponse response);
}
