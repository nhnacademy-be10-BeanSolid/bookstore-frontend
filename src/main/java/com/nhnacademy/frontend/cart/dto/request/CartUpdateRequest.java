package com.nhnacademy.frontend.cart.dto.request;


import jakarta.validation.constraints.Min;

public record CartUpdateRequest (@Min(1) int quantity){
}
