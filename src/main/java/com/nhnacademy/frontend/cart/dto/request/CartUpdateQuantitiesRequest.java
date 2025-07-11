package com.nhnacademy.frontend.cart.dto.request;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class CartUpdateQuantitiesRequest {
    private List<CartItemUpdateRequest> updates;
}
