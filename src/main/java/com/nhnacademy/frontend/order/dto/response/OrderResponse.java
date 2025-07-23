package com.nhnacademy.frontend.order.dto.response;

import java.io.Serializable;
import java.time.LocalDate;

public record OrderResponse(

        Long orderId,
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
) implements Serializable {}