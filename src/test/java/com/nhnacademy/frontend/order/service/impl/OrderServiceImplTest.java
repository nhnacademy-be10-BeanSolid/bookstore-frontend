package com.nhnacademy.frontend.order.service.impl;

import com.nhnacademy.frontend.common.adapter.GuestAdapter;
import com.nhnacademy.frontend.common.adapter.dto.user.request.GuestCreateRequest;
import com.nhnacademy.frontend.order.adapter.OrderAdapter;
import com.nhnacademy.frontend.order.dto.request.CreateOrderRequest;
import com.nhnacademy.frontend.order.dto.request.UpdateOrderRequest;
import com.nhnacademy.frontend.order.dto.response.CreateOrderResponse;
import com.nhnacademy.frontend.order.dto.response.OrderResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willDoNothing;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderServiceImplTest {

    @Mock
    private OrderAdapter orderAdapter;

    @Mock
    private GuestAdapter guestAdapter;

    @InjectMocks
    private OrderServiceImpl orderService;

    @Test
    @DisplayName("주문 생성에 성공한다")
    void createOrder_Success() {
        // Given
        CreateOrderRequest request = new CreateOrderRequest();
        request.setCreateItemRequests(List.of(new CreateOrderRequest.CreateOrderItemRequest(1L, 1)));

        String orderNumber = "202507-abcdef-123456";
        CreateOrderResponse expectedResponse = new CreateOrderResponse(orderNumber, List.of(new CreateOrderResponse.CreateOrderItemResponse(1L, "책제목", 100, 1, true)));

        given(orderAdapter.createOrder(request)).willReturn(expectedResponse);

        // When
        CreateOrderResponse result = orderService.createOrder(request);

        // Then
        assertThat(result).isEqualTo(expectedResponse);
        verify(orderAdapter, times(1)).createOrder(request);
    }

    @Test
    @DisplayName("미완료된 주문 조회에 성공한다")
    void getUnfinishedOrder_Success() {
        // Given
        String orderNumber = "202507-abcdef-123456";
        CreateOrderResponse expectedResponse = new CreateOrderResponse(orderNumber, List.of(new CreateOrderResponse.CreateOrderItemResponse(1L, "책제목", 100, 1, true)));

        given(orderAdapter.getUnfinishedOrder(orderNumber)).willReturn(expectedResponse);

        // When
        CreateOrderResponse result = orderService.getUnfinishedOrder(orderNumber);

        // Then
        assertThat(result).isEqualTo(expectedResponse);
        assertThat(result.getOrderNumber()).isEqualTo(orderNumber);
        assertThat(result.getOrderItems()).hasSize(1);
        verify(orderAdapter).getUnfinishedOrder(orderNumber);
    }

    @Test
    @DisplayName("주문서 작성 페이지 완료 후 주문 업데이트에 성공한다")
    void updateOrder_MemberOrder_Success() {
        // Given
        String orderNumber = "202507-abcdef-123456";
        UpdateOrderRequest request = UpdateOrderRequest.builder()
                .receiverName("테스트사용자")
                .receiverPhoneNumber("010-1234-5678")
                .address("서울시 강남구")
                .wrappingRequests(List.of(new UpdateOrderRequest.WrappingRequest(1L, 1L)))
                .build();

        OrderResponse response = new OrderResponse(
                1L,
                orderNumber,
                1L,
                "PENDING",
                LocalDate.now(),
                10000L,
                "테스트사용자",
                "010-1234-5678",
                "서울시 강남구",
                LocalDate.now(),
                100);

        given(orderAdapter.updateOrder(orderNumber, request)).willReturn(response);

        // When
        OrderResponse result = orderService.updateOrder(orderNumber, request);

        // Then
        assertThat(result).isEqualTo(response);
        assertThat(result.orderNumber()).isEqualTo(orderNumber);
        assertThat(result.totalPrice()).isEqualTo(10000L);
        verify(orderAdapter).updateOrder(orderNumber, request);
        verifyNoInteractions(guestAdapter);
    }

    @Test
    @DisplayName("비회원 주문 수정에 성공한다 - 비회원 등록까지 완료")
    void updateOrder_GuestOrder_Success() {
        // Given
        String orderNumber = "202507-abcdef-123456";
        String guestPassword = "guest123";
        UpdateOrderRequest request = UpdateOrderRequest.builder()
                .receiverName("테스트사용자")
                .receiverPhoneNumber("010-1234-5678")
                .address("서울시 강남구")
                .wrappingRequests(List.of(new UpdateOrderRequest.WrappingRequest(1L, 1L)))
                .nonMemberPassword(guestPassword)
                .build();

        OrderResponse response = new OrderResponse(
                1L,
                orderNumber,
                1L,
                "PENDING",
                LocalDate.now(),
                10000L,
                "테스트사용자",
                "010-1234-5678",
                "서울시 강남구",
                LocalDate.now(),
                100);

        GuestCreateRequest expectedGuestRequest = new GuestCreateRequest(guestPassword, response.orderId());

        given(orderAdapter.updateOrder(anyString(), any(UpdateOrderRequest.class))).willReturn(response);
        willDoNothing().given(guestAdapter).registerGuest(expectedGuestRequest);

        // When
        OrderResponse result = orderService.updateOrder(orderNumber, request);

        // Then
        assertThat(result).isEqualTo(response);
        assertThat(result.orderNumber()).isEqualTo(orderNumber);
        assertThat(result.totalPrice()).isEqualTo(10000L);
        verify(orderAdapter).updateOrder(orderNumber, request);
        verify(guestAdapter).registerGuest(any(GuestCreateRequest.class));
    }

    @Test
    @DisplayName("주문 수정 - 비회원 비밀번호가 null인 경우")
    void updateOrder_NonMemberPasswordNull_NoGuestRegistration() {
        // Given
        String orderNumber = "ORDER123";
        UpdateOrderRequest request = UpdateOrderRequest.builder()
                .receiverName("테스트사용자")
                .receiverPhoneNumber("010-1234-5678")
                .address("서울시 강남구")
                .nonMemberPassword(null)
                .build();
        OrderResponse response = new OrderResponse(
                1L,
                orderNumber,
                1L,
                "PENDING",
                LocalDate.now(),
                10000L,
                "테스트사용자",
                "010-1234-5678",
                "서울시 강남구",
                LocalDate.now(),
                100);

        given(orderAdapter.updateOrder(orderNumber, request)).willReturn(response);

        // When
        OrderResponse result = orderService.updateOrder(orderNumber, request);

        // Then
        assertThat(result).isEqualTo(response);
        verify(orderAdapter).updateOrder(orderNumber, request);
        verifyNoInteractions(guestAdapter);
    }

    @Test
    @DisplayName("주문 수정 - 비회원 비밀번호가 빈 문자열인 경우")
    void updateOrder_NonMemberPasswordEmpty_NoGuestRegistration() {
        // Given
        String orderNumber = "ORDER123";
        UpdateOrderRequest request = UpdateOrderRequest.builder()
                .receiverName("테스트사용자")
                .receiverPhoneNumber("010-1234-5678")
                .address("서울시 강남구")
                .nonMemberPassword("")
                .build();

        OrderResponse response = new OrderResponse(
                1L,
                orderNumber,
                1L,
                "PENDING",
                LocalDate.now(),
                10000L,
                "테스트사용자",
                "010-1234-5678",
                "서울시 강남구",
                LocalDate.now(),
                100);

        given(orderAdapter.updateOrder(orderNumber, request)).willReturn(response);

        // When
        OrderResponse result = orderService.updateOrder(orderNumber, request);

        // Then
        assertThat(result).isEqualTo(response);
        verify(orderAdapter).updateOrder(orderNumber, request);
        verifyNoInteractions(guestAdapter);
    }
}
