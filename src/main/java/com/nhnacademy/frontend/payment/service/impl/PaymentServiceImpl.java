package com.nhnacademy.frontend.payment.service.impl;

import com.nhnacademy.frontend.common.adapter.UserAdapter;
import com.nhnacademy.frontend.common.adapter.dto.user.response.ResponseUser;
import com.nhnacademy.frontend.payment.adapter.PaymentAdapter;
import com.nhnacademy.frontend.payment.dto.request.PaymentApprovalRequestDto;
import com.nhnacademy.frontend.payment.dto.request.PaymentRequestDto;
import com.nhnacademy.frontend.payment.dto.response.PaymentResponseDto;
import com.nhnacademy.frontend.payment.service.PaymentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentServiceImpl implements PaymentService {

    private final PaymentAdapter paymentAdapter;
    private final UserAdapter userAdapter;


    @Override
    public PaymentResponseDto requestPayment(PaymentRequestDto req) {
        log.info("[Payment] requestPayment orderId={}, amount={}",
                req.getOrderId(), req.getPayAmount());
        return paymentAdapter.ready(req.getOrderId(), req);
    }


    @Override
    public void confirmSuccess(PaymentApprovalRequestDto dto) {
        log.info("[Payment] confirmSuccess paymentKey={}, orderId={}, amount={}", dto.getPaymentKey(), dto.getOrderId(), dto.getAmount());
        paymentAdapter.confirmSuccess(dto.getPaymentKey(), dto.getOrderId(), dto.getAmount());
    }

    @Override
    public void confirmFail(String paymentKey, String orderId) {
        log.info("[Payment] confirmFail paymentKey={}, orderId={}", paymentKey, orderId);
        paymentAdapter.confirmFail(paymentKey, orderId);
    }

    @Override
    public Long getCurrentUserPoints() {
        ResponseEntity<ResponseUser> userInfoResponse = userAdapter.getUserInfo();
        if (userInfoResponse != null && userInfoResponse.getBody() != null) {
            return (long) userInfoResponse.getBody().getUserPoint();
        }
        return 0L;
    }
}

