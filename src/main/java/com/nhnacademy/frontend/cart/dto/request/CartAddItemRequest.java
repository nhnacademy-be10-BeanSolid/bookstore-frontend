package com.nhnacademy.frontend.cart.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record CartAddItemRequest(
        @NotNull long itemId,
        @Min(1) int quantity
) {
}
