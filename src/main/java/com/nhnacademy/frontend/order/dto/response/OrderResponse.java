package com.nhnacademy.frontend.order.dto.response;

import java.time.LocalDate;

public record OrderResponse(

        String orderNumber,
        Long userNo,
        String status,
        LocalDate orderDate,
        Long totalPrice,
        String receiverName,
        String receiverPhoneNumber,
        String address,
        LocalDate requestedDeliveryDate,
        Integer shippingFee
) {}