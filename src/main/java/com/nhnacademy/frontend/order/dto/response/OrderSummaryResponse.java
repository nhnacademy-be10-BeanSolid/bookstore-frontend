package com.nhnacademy.frontend.order.dto.response;

import java.time.LocalDate;

public record OrderSummaryResponse(
        LocalDate orderDate,
        String orderId,
        String receiverName,
        Long totalPrice
) {}