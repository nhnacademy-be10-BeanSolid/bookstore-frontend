package com.nhnacademy.frontend.cart.dto;

import com.nhnacademy.frontend.cart.dto.view.CartItemViewModel;
import java.util.List;

public record CartViewResponse(List<CartItemViewModel> cartItems, String newGuestUuid) {
}
