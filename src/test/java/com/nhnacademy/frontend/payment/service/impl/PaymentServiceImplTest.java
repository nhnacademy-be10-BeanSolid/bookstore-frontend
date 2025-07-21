package com.nhnacademy.frontend.payment.service.impl;

import com.nhnacademy.frontend.payment.adapter.PaymentAdapter;
import com.nhnacademy.frontend.payment.dto.request.PaymentApprovalRequestDto;
import com.nhnacademy.frontend.payment.dto.request.PaymentRequestDto;
import com.nhnacademy.frontend.payment.dto.response.PaymentResponseDto;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PaymentServiceImplTest {

    @Mock
    private PaymentAdapter paymentAdapter;

    @InjectMocks
    private PaymentServiceImpl paymentService;

    @Test
    void testRequestPayment() {
        PaymentRequestDto requestDto = new PaymentRequestDto();
        requestDto.setOrderId("orderId");
        requestDto.setPayAmount(10000L);
        requestDto.setPayName("orderName");

        PaymentResponseDto expectedResponse = new PaymentResponseDto();
        expectedResponse.setPaymentKey("paymentKey");
        expectedResponse.setOrderId("orderId");
        expectedResponse.setPayAmount(10000L);

        when(paymentAdapter.ready(requestDto.getOrderId(), requestDto)).thenReturn(expectedResponse);

        PaymentResponseDto actualResponse = paymentService.requestPayment(requestDto);

        assertEquals(expectedResponse, actualResponse);
        verify(paymentAdapter, times(1)).ready(requestDto.getOrderId(), requestDto);
    }

    @Test
    void testConfirmSuccess() {
        PaymentApprovalRequestDto requestDto = new PaymentApprovalRequestDto("paymentKey", "orderId", 10000L);

        when(paymentAdapter.confirmSuccess(requestDto.getPaymentKey(), requestDto.getOrderId(), requestDto.getAmount())).thenReturn(requestDto);

        paymentService.confirmSuccess(requestDto);

        verify(paymentAdapter, times(1)).confirmSuccess(requestDto.getPaymentKey(), requestDto.getOrderId(), requestDto.getAmount());
    }

    @Test
    void testConfirmFail() {
        String paymentKey = "paymentKey";
        String orderId = "orderId";

        doNothing().when(paymentAdapter).confirmFail(paymentKey, orderId);

        paymentService.confirmFail(paymentKey, orderId);

        verify(paymentAdapter, times(1)).confirmFail(paymentKey, orderId);
    }
}