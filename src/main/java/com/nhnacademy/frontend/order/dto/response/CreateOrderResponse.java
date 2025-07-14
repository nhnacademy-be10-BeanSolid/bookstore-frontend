package com.nhnacademy.frontend.order.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.io.Serializable;
import java.util.List;

@Getter
@AllArgsConstructor
public class CreateOrderResponse implements Serializable {

    private String orderNumber;
    private List<CreateOrderItemResponse> orderItems;

    @Getter
    @AllArgsConstructor
    public static class CreateOrderItemResponse implements Serializable {

        private Long bookId;
        private String bookTitle;
        private Integer unitPrice;
        private Integer quantity;
        private Boolean wrappable;
    }
}
