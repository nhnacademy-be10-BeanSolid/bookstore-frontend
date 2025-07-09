package com.nhnacademy.frontend.common.adapter;

import com.nhnacademy.frontend.payment.domain.request.PaymentRequestDto;
import com.nhnacademy.frontend.payment.domain.response.PaymentResponseDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

@FeignClient(
        name      = "gateway-service",
        contextId = "paymentsAdapter",
        path      = "/order-api/api/v1/payments"
)
public interface PaymentAdapter {


    @PostMapping("/toss/{orderId}")
    PaymentResponseDto ready(
            @PathVariable("orderId") String orderId,
            @RequestBody              PaymentRequestDto dto
    );


    @GetMapping("/success")
    void confirmSuccess(
            @RequestParam("paymentKey") String paymentKey,
            @RequestParam("orderId")    String orderId,
            @RequestParam("amount")     Long amount
    );


    @GetMapping("/fail")
    void confirmFail(
            @RequestParam("paymentKey") String paymentKey,
            @RequestParam("orderId")    String orderId
    );
}