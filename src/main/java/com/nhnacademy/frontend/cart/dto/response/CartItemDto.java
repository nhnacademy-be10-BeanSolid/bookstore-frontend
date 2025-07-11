package com.nhnacademy.frontend.cart.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CartItemDto {
    private long itemId;
    private int quantity;
}
