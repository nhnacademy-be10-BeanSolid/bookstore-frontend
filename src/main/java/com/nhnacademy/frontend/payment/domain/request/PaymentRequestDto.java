package com.nhnacademy.frontend.payment.domain.request;

import com.nhnacademy.frontend.payment.domain.PayType;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class PaymentRequestDto {
    @NotNull
    private String orderId;

    @NotNull
    private Long payAmount;

    @NotNull
    private PayType payType;

    @NotNull
    private String payName;
    private String successUrl;
    private String failUrl;
}
