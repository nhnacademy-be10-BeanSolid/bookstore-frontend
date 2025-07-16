package com.nhnacademy.frontend.payment.service;

import com.nhnacademy.frontend.payment.dto.request.PaymentApprovalRequestDto;
import com.nhnacademy.frontend.payment.dto.request.PaymentRequestDto;
import com.nhnacademy.frontend.payment.dto.response.PaymentResponseDto;

public interface PaymentService {

    PaymentResponseDto requestPayment(PaymentRequestDto req);

    void confirmSuccess(PaymentApprovalRequestDto dto);

    void confirmFail(String paymentKey, String orderId);

    Long getCurrentUserPoints();
}