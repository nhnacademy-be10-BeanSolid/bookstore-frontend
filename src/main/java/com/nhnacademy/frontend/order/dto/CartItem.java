package com.nhnacademy.frontend.order.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 장바구니에 담긴 도서를 주문하거나 바로 주문하기 버튼을 눌렀을 때,
 * 주문 페이지에 상품을 보여주기 위한 임시 클래스.
 */
@Getter
@AllArgsConstructor
public class CartItem {

    private Long bookId;
    private String title;
    private Integer quantity;
    private Long price;
}
