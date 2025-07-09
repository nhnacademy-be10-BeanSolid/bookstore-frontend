package com.nhnacademy.frontend.payment.service;

import com.nhnacademy.frontend.payment.domain.request.PaymentRequestDto;
import com.nhnacademy.frontend.payment.domain.response.PaymentResponseDto;

public interface PaymentService {

    PaymentResponseDto requestPayment(PaymentRequestDto req);

    void confirmSuccess(String paymentKey, String orderId);

    void confirmFail(String paymentKey, String orderId);
}