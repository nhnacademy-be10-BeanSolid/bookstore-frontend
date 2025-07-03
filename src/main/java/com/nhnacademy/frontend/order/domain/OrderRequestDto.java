package com.nhnacademy.frontend.order.domain;

import lombok.*;

import java.io.Serializable;
import java.util.List;

@Data
public class OrderRequestDto {

    private String receiverName;
    private String receiverPhoneNumber;
    private String shippingAddress;
    private String orderMessage;
    private Long totalAmount;
    private List<OrderItemDto> orderItems;

    @Data
    public static class OrderItemDto {

        private Long bookId;
        private String bookTitle;
        private Integer quantity;
        private Long price;
        private Long wrappingId;
    }
}