package com.nhnacademy.frontend.payment.service;

import com.nhnacademy.frontend.payment.domain.request.PaymentRequestDto;
import com.nhnacademy.frontend.payment.domain.response.PaymentResponseDto;

public interface PaymentService {

    PaymentResponseDto requestPayment(PaymentRequestDto req);

    void confirmSuccess(String paymentKey, String orderId, Long amount);

    void confirmFail(String paymentKey, String orderId);



}