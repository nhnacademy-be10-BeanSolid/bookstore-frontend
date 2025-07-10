package com.nhnacademy.frontend.cart.dto.request;

import lombok.Getter;
import lombok.Setter;

import java.util.Map;

@Getter
@Setter
public class CartUpdateQuantitiesRequest {
    private Map<Long, Integer> quantities;
}
