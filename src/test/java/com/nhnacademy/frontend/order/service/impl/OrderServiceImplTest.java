package com.nhnacademy.frontend.order.service.impl;

import com.nhnacademy.frontend.common.adapter.OrderAdapter;
import com.nhnacademy.frontend.order.dto.request.OrderRequest;
import com.nhnacademy.frontend.order.dto.response.OrderResponse;
import com.nhnacademy.frontend.order.dto.response.OrderSummaryResponse;
import feign.FeignException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("OrderServiceImpl 단위 테스트")
class OrderServiceImplTest {

    @Mock
    private OrderAdapter orderAdapter;

    @InjectMocks
    private OrderServiceImpl orderService;

    private OrderRequest orderRequest;
    private OrderResponse expectedResponse;

    @BeforeEach
    void setUp() {
        orderRequest = createOrderRequest();
        expectedResponse = createOrderResponse();
    }

    @Test
    @DisplayName("주문 생성 - 성공")
    void createOrder_Success() {
        // given
        when(orderAdapter.createOrder(orderRequest)).thenReturn(expectedResponse);

        // when
        OrderResponse result = orderService.createOrder(orderRequest);

        // then
        assertNotNull(result);
        assertEquals(expectedResponse.id(), result.id());
        assertEquals(expectedResponse.orderId(), result.orderId());
        assertEquals(expectedResponse.status(), result.status());
        assertEquals(expectedResponse.receiverName(), result.receiverName());
        assertEquals(expectedResponse.totalAmount(), result.totalAmount());
        
        verify(orderAdapter).createOrder(orderRequest);
    }

    @Test
    @DisplayName("주문 생성 - OrderAdapter 호출 실패")
    void createOrder_AdapterCallFailed() {
        // given
        when(orderAdapter.createOrder(orderRequest))
                .thenThrow(new RuntimeException("Network error"));

        // when & then
        RuntimeException exception = assertThrows(RuntimeException.class, () -> 
            orderService.createOrder(orderRequest)
        );
        
        assertEquals("Network error", exception.getMessage());
        verify(orderAdapter).createOrder(orderRequest);
    }

    @Test
    @DisplayName("주문 생성 - FeignException 발생")
    void createOrder_FeignExceptionThrown() {
        // given
        FeignException feignException = mock(FeignException.class);
        when(feignException.getMessage()).thenReturn("Backend service error");
        when(orderAdapter.createOrder(orderRequest)).thenThrow(feignException);

        // when & then
        FeignException exception = assertThrows(FeignException.class, () -> 
            orderService.createOrder(orderRequest)
        );
        
        assertEquals("Backend service error", exception.getMessage());
        verify(orderAdapter).createOrder(orderRequest);
    }

    @Test
    @DisplayName("주문 생성 - 메서드 호출 횟수 검증")
    void createOrder_MethodCallCount() {
        // given
        when(orderAdapter.createOrder(orderRequest)).thenReturn(expectedResponse);

        // when
        orderService.createOrder(orderRequest);
        orderService.createOrder(orderRequest);

        // then
        verify(orderAdapter, times(2)).createOrder(orderRequest);
    }

    @Test
    @DisplayName("주문 생성 - 정확한 파라미터 전달 검증")
    void createOrder_ParameterValidation() {
        // given
        OrderRequest specificRequest = createOrderRequest();
        specificRequest.setReceiverName("테스트 사용자");
        
        when(orderAdapter.createOrder(any(OrderRequest.class))).thenReturn(expectedResponse);

        // when
        orderService.createOrder(specificRequest);

        // then
        verify(orderAdapter).createOrder(argThat(request -> 
            "테스트 사용자".equals(request.getReceiverName()) &&
            request.getOrderItems().size() == 1 &&
            request.getZipCode().equals("12345")
        ));
    }

    @Test
    @DisplayName("주문 전체 조회 - 성공")
    void getAllOrders_Success() {
        // given
        Page<OrderSummaryResponse> expectedPage = createOrderSummaryPage();
        when(orderAdapter.getAllOrdersByUserId()).thenReturn(expectedPage);

        // when
        Page<OrderSummaryResponse> result = orderService.getAllOrders();

        // then
        assertNotNull(result);
        assertEquals(expectedPage.getTotalElements(), result.getTotalElements());
        assertEquals(expectedPage.getContent().size(), result.getContent().size());
        assertEquals(expectedPage.getContent().get(0).orderId(), result.getContent().get(0).orderId());
        assertEquals(expectedPage.getContent().get(0).receiverName(), result.getContent().get(0).receiverName());
        
        verify(orderAdapter).getAllOrdersByUserId();
    }

    @Test
    @DisplayName("주문 전체 조회 - OrderAdapter 호출 실패")
    void getAllOrders_AdapterCallFailed() {
        // given
        when(orderAdapter.getAllOrdersByUserId()).thenThrow(new RuntimeException("Network error"));

        // when & then
        RuntimeException exception = assertThrows(RuntimeException.class, () -> 
            orderService.getAllOrders()
        );
        
        assertEquals("Network error", exception.getMessage());
        verify(orderAdapter).getAllOrdersByUserId();
    }

    @Test
    @DisplayName("주문 상세 조회 - 성공")
    void getOrder_Success() {
        // given
        String orderId = "190001-abcabc-123123";
        OrderResponse expectedOrder = createOrderResponse();
        when(orderAdapter.getOrder(orderId)).thenReturn(expectedOrder);

        // when
        OrderResponse result = orderService.getOrder(orderId);

        // then
        assertNotNull(result);
        assertEquals(expectedOrder.id(), result.id());
        assertEquals(expectedOrder.orderId(), result.orderId());
        assertEquals(expectedOrder.status(), result.status());
        assertEquals(expectedOrder.receiverName(), result.receiverName());
        assertEquals(expectedOrder.totalAmount(), result.totalAmount());
        
        verify(orderAdapter).getOrder(orderId);
    }

    @Test
    @DisplayName("주문 상세 조회 - OrderAdapter 호출 실패")
    void getOrder_AdapterCallFailed() {
        // given
        String orderId = "190001-abcabc-123123";
        when(orderAdapter.getOrder(orderId)).thenThrow(new RuntimeException("Order not found"));

        // when & then
        RuntimeException exception = assertThrows(RuntimeException.class, () -> 
            orderService.getOrder(orderId)
        );
        
        assertEquals("Order not found", exception.getMessage());
        verify(orderAdapter).getOrder(orderId);
    }

    @Test
    @DisplayName("주문 상세 조회 - 올바른 파라미터 전달 검증")
    void getOrder_ParameterValidation() {
        // given
        String orderId = "test-order-id-123";
        OrderResponse expectedOrder = createOrderResponse();
        when(orderAdapter.getOrder(orderId)).thenReturn(expectedOrder);

        // when
        orderService.getOrder(orderId);

        // then
        verify(orderAdapter).getOrder(orderId);
    }

    private OrderRequest createOrderRequest() {
        OrderRequest orderRequest = new OrderRequest();
        orderRequest.setReceiverName("홍길동");
        orderRequest.setReceiverPhoneNumber("01012345678");
        orderRequest.setZipCode("12345");
        orderRequest.setBaseAddress("서울시 강남구");
        orderRequest.setDetailAddress("101동 101호");
        orderRequest.setRequestedDeliveryDate(LocalDate.now().plusDays(3));
        
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

    private Page<OrderSummaryResponse> createOrderSummaryPage() {
        List<OrderSummaryResponse> orderSummaries = List.of(
                new OrderSummaryResponse(
                        LocalDate.of(3000, 1, 1),
                        "190001-abcabc-123123",
                        "홍길동",
                        23000L
                ),
                new OrderSummaryResponse(
                        LocalDate.of(3000, 1, 2),
                        "190002-defdef-456456",
                        "김철수",
                        15000L
                )
        );
        
        Pageable pageable = PageRequest.of(0, 10);
        return new PageImpl<>(orderSummaries, pageable, orderSummaries.size());
    }
}