package com.nhnacademy.frontend.common.adapter;

import com.nhnacademy.frontend.payment.domain.request.PaymentApprovalRequestDto;
import com.nhnacademy.frontend.payment.domain.request.PaymentRequestDto;
import com.nhnacademy.frontend.payment.domain.response.PaymentResponseDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

@FeignClient(name = "gateway-service", contextId = "paymentsAdapter")
public interface PaymentAdapter {

    /** 결제 준비(redirect URL 받기) */
    @PostMapping("/order-api/api/v1/payments/toss/{orderId}")
    PaymentResponseDto ready(@PathVariable("orderId") String orderId,
                             @RequestBody PaymentRequestDto dto);

    /** 결제 성공 콜백 → 최종 confirm */
    @GetMapping("/order-api/api/v1/payments/success")
    PaymentApprovalRequestDto confirmSuccess(@RequestParam("paymentKey") String paymentKey,
                                           @RequestParam("orderId") String orderId,
                                             @RequestParam("amount") long amount);

    /** 결제 실패 콜백 → 실패 처리 */
    @PostMapping("/order-api/api/v1/payments/fail")
    void confirmFail(@RequestParam("paymentKey") String paymentKey,
                     @RequestParam("orderId")    String orderId);
}