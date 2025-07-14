package com.nhnacademy.frontend.cart.dto.response;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class CartResponse {
    private long cartId;
    private List<CartItemDto> items;
}