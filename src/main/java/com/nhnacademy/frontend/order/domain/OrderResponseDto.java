package com.nhnacademy.frontend.order.domain;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class OrderResponseDto {
    private Long orderId;
    private String orderNumber;
    private String recipientName;
    private String recipientPhone;
    private String shippingAddress;
    private String orderMessage;
    private BigDecimal totalAmount;
    private String orderStatus;
    private LocalDateTime orderDate;
}