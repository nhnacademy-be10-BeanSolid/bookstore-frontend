package com.nhnacademy.frontend.order.dto.response;

import java.time.LocalDate;

public record OrderSummaryResponse(

        LocalDate orderDate,
        String orderNumber,
        String receiverName,
        Long totalPrice,
        String status
) {}