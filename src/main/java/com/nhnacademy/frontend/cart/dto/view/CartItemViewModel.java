package com.nhnacademy.frontend.cart.dto.view;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CartItemViewModel {
    private Long bookId;
    private String bookTitle;
    private int quantity;
    private long price;
}
