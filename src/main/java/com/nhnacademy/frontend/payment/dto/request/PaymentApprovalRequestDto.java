package com.nhnacademy.frontend.payment.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class PaymentApprovalRequestDto {

    private String paymentKey;
    private String orderId;
    private long amount;
}
