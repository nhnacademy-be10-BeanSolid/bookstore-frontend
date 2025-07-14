package com.nhnacademy.frontend.payment.dto.response;

import lombok.Data;

@Data
public class PaymentResponseDto {
    private Long paymentId;
    private String orderId;
    private String payType;
    private Long payAmount;
    private String payName;
    private String paymentStatus;
    private String paymentKey;
    private String successUrl;
    private String failUrl;
    private String redirectUrl;
}