package com.nhnacademy.frontend.payment.controller;

import com.nhnacademy.frontend.common.advice.GlobalModelAttributeAdvice;
import com.nhnacademy.frontend.common.service.UserService;
import com.nhnacademy.frontend.payment.dto.request.PaymentRequestDto;
import com.nhnacademy.frontend.payment.dto.response.PaymentResponseDto;
import com.nhnacademy.frontend.payment.service.PaymentService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class PaymentControllerTest {

    private MockMvc mockMvc;

    @Mock
    private PaymentService paymentService;

    @Mock
    private UserService userService;

    @InjectMocks
    private PaymentController paymentController;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(paymentController)
                .setControllerAdvice(new GlobalModelAttributeAdvice())
                .build();
    }

    @Test
    void testShowForm() throws Exception {
        when(userService.getCurrentUserPoints()).thenReturn(1000L);

        mockMvc.perform(get("/payments/form")
                        .param("orderId", "testOrderId")
                        .param("amount", "10000"))
                .andExpect(status().isOk())
                .andExpect(view().name("payments/form"))
                .andExpect(model().attributeExists("paymentRequest"))
                .andExpect(model().attribute("shippingFee", 5000))
                .andExpect(model().attribute("currentPoints", 1000L));
    }

    @Test
    void testRequestPayment() throws Exception {
        PaymentResponseDto responseDto = new PaymentResponseDto();
        responseDto.setRedirectUrl("http://localhost:8080/redirect");

        when(paymentService.requestPayment(any(PaymentRequestDto.class))).thenReturn(responseDto);

        mockMvc.perform(post("/payments")
                        .param("orderId", "testOrderId")
                        .param("payAmount", "10000")
                        .param("payName", "testPayName")
                        .param("payType", "CARD")) // Added payType
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("http://localhost:8080/redirect"));
    }

    @Test
    void testSuccess() throws Exception {
        mockMvc.perform(get("/payments/success")
                        .param("paymentKey", "testPaymentKey")
                        .param("orderId", "testOrderId")
                        .param("amount", "10000"))
                .andExpect(status().isOk())
                .andExpect(view().name("payments/success"))
                .andExpect(model().attribute("paymentKey", "testPaymentKey"))
                .andExpect(model().attribute("orderId", "testOrderId"))
                .andExpect(model().attribute("amount", 10000L));
    }

    @Test
    void testFail() throws Exception {
        mockMvc.perform(get("/payments/fail")
                        .param("paymentKey", "testPaymentKey")
                        .param("orderId", "testOrderId")
                        .param("message", "testMessage")
                        .param("amount", "10000")) // Added amount for Thymeleaf
                .andExpect(status().isOk())
                .andExpect(view().name("payments/fail"))
                .andExpect(model().attribute("paymentKey", "testPaymentKey"))
                .andExpect(model().attribute("orderId", "testOrderId"))
                .andExpect(model().attribute("message", "testMessage"));
                
    }
}