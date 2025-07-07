package com.nhnacademy.frontend.order.dto.response;

import java.time.LocalDate;

public record OrderResponse(
        Long id,
        String orderId,
        String status,
        LocalDate orderDate,
        String receiverName,
        String receiverPhoneNumber,
        String address,
        LocalDate requestedDeliveryDate,
        Integer deliveryFee,
        Long totalAmount
) {}