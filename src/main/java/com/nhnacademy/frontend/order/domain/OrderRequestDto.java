package com.nhnacademy.frontend.order.domain;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class OrderRequestDto {
    private String recipientName;
    private String recipientPhone;
    private String shippingAddress;
    private String orderMessage;
    private BigDecimal totalAmount;
    private List<OrderItemDto> orderItems;
    
    @Data
    public static class OrderItemDto {
        private Long bookId;
        private String bookTitle;
        private Integer quantity;
        private BigDecimal price;
        private BigDecimal totalPrice;
    }
}