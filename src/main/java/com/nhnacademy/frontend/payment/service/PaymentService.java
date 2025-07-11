package com.nhnacademy.frontend.payment.service;

import com.nhnacademy.frontend.payment.domain.request.PaymentApprovalRequestDto;
import com.nhnacademy.frontend.payment.domain.request.PaymentRequestDto;
import com.nhnacademy.frontend.payment.domain.response.PaymentResponseDto;

public interface PaymentService {

    PaymentResponseDto requestPayment(PaymentRequestDto req);

    void confirmSuccess(PaymentApprovalRequestDto dto);

    void confirmFail(String paymentKey, String orderId);



}