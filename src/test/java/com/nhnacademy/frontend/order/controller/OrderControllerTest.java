package com.nhnacademy.frontend.order.controller;

import com.nhnacademy.frontend.order.dto.request.OrderRequest;
import com.nhnacademy.frontend.order.dto.response.OrderResponse;
import com.nhnacademy.frontend.order.service.OrderService;
import com.nhnacademy.frontend.user.exception.ValidationFailedException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.validation.BindingResult;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.util.Collections;

import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("OrderController 단위 테스트")
class OrderControllerTest {

    @Mock
    private OrderService orderService;

    @Mock
    private BindingResult bindingResult;

    @Mock
    private RedirectAttributes redirectAttributes;

    @InjectMocks
    private OrderController orderController;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(orderController).build();
    }

    @Test
    @DisplayName("주문 페이지 조회 - 성공")
    void orderPage_Success() throws Exception {
        // when & then
        mockMvc.perform(get("/orders"))
                .andExpect(status().isOk())
                .andExpect(view().name("/order/order"))
                .andExpect(model().attributeExists("items"))
                .andExpect(model().attribute("items", hasSize(2)));
    }

    @Test
    @DisplayName("주문 생성 - 성공")
    void createOrder_Success() {
        // given
        OrderRequest orderRequest = createOrderRequest();
        OrderResponse orderResponse = createOrderResponse();
        
        when(bindingResult.hasErrors()).thenReturn(false);
        when(orderService.createOrder(orderRequest)).thenReturn(orderResponse);

        // when
        String result = orderController.createOrder(orderRequest, bindingResult, redirectAttributes);

        // then
        assertEquals("redirect:/orders/tempPay", result);
        verify(orderService).createOrder(orderRequest);
        verify(redirectAttributes).addFlashAttribute("orderResponse", orderResponse);
        verify(redirectAttributes, never()).addFlashAttribute(eq("errorMessage"), any());
    }

    @Test
    @DisplayName("주문 생성 - 유효성 검증 실패")
    void createOrder_ValidationFailed() {
        // given
        OrderRequest orderRequest = createOrderRequest();
        when(bindingResult.hasErrors()).thenReturn(true);

        // when & then
        assertThrows(ValidationFailedException.class, () -> 
            orderController.createOrder(orderRequest, bindingResult, redirectAttributes)
        );
        
        verify(orderService, never()).createOrder(any());
        verify(redirectAttributes, never()).addFlashAttribute(any(), any());
    }

    @Test
    @DisplayName("주문 생성 - 서비스 예외 발생")
    void createOrder_ServiceException() {
        // given
        OrderRequest orderRequest = createOrderRequest();
        String errorMessage = "주문 처리 중 오류가 발생했습니다";
        
        when(bindingResult.hasErrors()).thenReturn(false);
        when(orderService.createOrder(orderRequest)).thenThrow(new RuntimeException(errorMessage));

        // when
        String result = orderController.createOrder(orderRequest, bindingResult, redirectAttributes);

        // then
        assertEquals("redirect:/orders", result);
        verify(orderService).createOrder(orderRequest);
        verify(redirectAttributes).addFlashAttribute("errorMessage", errorMessage);
        verify(redirectAttributes, never()).addFlashAttribute(eq("orderResponse"), any());
    }

    @Test
    @DisplayName("결제 페이지 조회 - 성공")
    void payPage_Success() throws Exception {
        // when & then
        mockMvc.perform(get("/orders/tempPay"))
                .andExpect(status().isOk())
                .andExpect(view().name("/order/tempPay"));
    }

    @Test
    @DisplayName("주문 생성 요청 - POST 매핑 테스트 (Validation 성공)")
    void createOrder_PostMapping() throws Exception {
        // given
        OrderResponse orderResponse = createOrderResponse();
        when(orderService.createOrder(any(OrderRequest.class))).thenReturn(orderResponse);

        // when & then
        mockMvc.perform(post("/orders")
                .param("receiverName", "홍길동")
                .param("receiverPhoneNumber", "01012345678")
                .param("zipCode", "12345")
                .param("baseAddress", "서울시 강남구")
                .param("detailAddress", "101동 101호")
                .param("requestedDeliveryDate", "3000-01-04")
                .param("orderItems[0].bookId", "1")
                .param("orderItems[0].quantity", "2")
                .param("orderItems[0].price", "10000")
                .param("orderItems[0].wrappingId", "1"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/orders/tempPay"));
    }

    @Test
    @DisplayName("주문 생성 요청 - POST 매핑 Validation 실패")
    void createOrder_PostMapping_ValidationFailed() throws Exception {
        // when & then - receiverName 누락으로 validation 실패
        mockMvc.perform(post("/orders")
                .param("receiverPhoneNumber", "01012345678")
                .param("zipCode", "12345")
                .param("baseAddress", "서울시 강남구")
                .param("orderItems[0].bookId", "1")
                .param("orderItems[0].quantity", "2")
                .param("orderItems[0].price", "10000"))
                .andExpect(status().is4xxClientError());
    }

    private OrderRequest createOrderRequest() {
        OrderRequest orderRequest = new OrderRequest();
        orderRequest.setReceiverName("홍길동");
        orderRequest.setReceiverPhoneNumber("01012345678");
        orderRequest.setZipCode("12345");
        orderRequest.setBaseAddress("서울시 강남구");
        orderRequest.setDetailAddress("101동 101호");
        orderRequest.setRequestedDeliveryDate(LocalDate.of(3000, 1, 1).plusDays(3));
        
        OrderRequest.OrderItem orderItem = new OrderRequest.OrderItem();
        orderItem.setBookId(1L);
        orderItem.setQuantity(2);
        orderItem.setPrice(10000L);
        orderItem.setWrappingId(1L);
        
        orderRequest.setOrderItems(Collections.singletonList(orderItem));
        return orderRequest;
    }

    private OrderResponse createOrderResponse() {
        return new OrderResponse(
                1L,
                "190001-abcabc-123123",
                "PENDING",
                LocalDate.of(3000, 1, 1),
                "홍길동",
                "01012345678",
                "서울시 강남구 101동 101호",
                LocalDate.of(3000, 1, 1).plusDays(3),
                3000,
                23000L
        );
    }
}