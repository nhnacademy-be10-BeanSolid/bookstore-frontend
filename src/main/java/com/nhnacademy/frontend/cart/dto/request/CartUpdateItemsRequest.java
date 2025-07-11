package com.nhnacademy.frontend.cart.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public record CartUpdateItemsRequest(
        @NotNull
        @Valid
        List<CartItemUpdate> items
) {
    public record CartItemUpdate(
            @NotNull Long bookId,
            @Min(1) int quantity
    ) {}
}
