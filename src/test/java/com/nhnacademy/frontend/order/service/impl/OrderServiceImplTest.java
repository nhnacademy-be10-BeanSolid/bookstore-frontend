package com.nhnacademy.frontend.order.service.impl;

import com.nhnacademy.frontend.adapter.OrderAdapter;
import com.nhnacademy.frontend.order.dto.request.OrderRequest;
import com.nhnacademy.frontend.order.dto.response.OrderResponse;
import feign.FeignException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Collections;

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
        assertEquals(expectedResponse.orderNumber(), result.orderNumber());
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
}