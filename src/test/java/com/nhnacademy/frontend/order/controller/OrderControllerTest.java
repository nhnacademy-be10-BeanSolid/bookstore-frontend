package com.nhnacademy.frontend.order.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nhnacademy.frontend.auth.filter.JwtAuthenticationFilter;
import com.nhnacademy.frontend.common.adapter.UserAdapter;
import com.nhnacademy.frontend.common.adapter.dto.user.response.ResponseUser;
import com.nhnacademy.frontend.order.dto.request.CreateOrderRequest;
import com.nhnacademy.frontend.order.dto.request.UpdateOrderRequest;
import com.nhnacademy.frontend.order.dto.response.CreateOrderResponse;
import com.nhnacademy.frontend.order.dto.response.OrderDetailResponse;
import com.nhnacademy.frontend.order.dto.response.OrderResponse;
import com.nhnacademy.frontend.order.dto.response.OrderSummaryResponse;
import com.nhnacademy.frontend.order.exception.OrderNotFoundException;
import com.nhnacademy.frontend.order.service.OrderService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(
    controllers = OrderController.class,
    excludeFilters = @ComponentScan.Filter(
        type = FilterType.ASSIGNABLE_TYPE,
        classes = JwtAuthenticationFilter.class
    )
)
@AutoConfigureMockMvc(addFilters = false)
class OrderControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private OrderService orderService;
    @MockBean
    private UserAdapter userAdapter;
    @MockBean
    private RedisConnectionFactory redisConnectionFactory;

    @Test
    @DisplayName("회원의 주문서 작성 페이지 응답에 성공하면 200을 응답한다")
    void orderPage_LoggedInUser_Success() throws Exception {
        // Given
        String orderNumber = "202507-abcdef-123456";
        CreateOrderResponse.CreateOrderItemResponse item = CreateOrderResponse.CreateOrderItemResponse.builder()
                .bookId(1L)
                .bookTitle("Test Book")
                .quantity(2)
                .unitPrice(15000)
                .wrappable(true)
                .build();
        CreateOrderResponse orderResponse = new CreateOrderResponse(orderNumber, List.of(item));
        ResponseUser user = ResponseUser.builder()
                .userName("테스트사용자")
                .userPhoneNumber("010-1234-5678")
                .build();

        given(orderService.getUnfinishedOrder(orderNumber)).willReturn(orderResponse);
        given(userAdapter.getUserInfo()).willReturn(ResponseEntity.ok(user));

        // When & Then
        mockMvc.perform(get("/orders/{orderNumber}/input-detail", orderNumber)
                .flashAttr("isLoggedIn", true))
                .andExpect(status().isOk())
                .andExpect(view().name("order/order"))
                .andExpect(model().attribute("orderNumber", orderNumber))
                .andExpect(model().attribute("items", List.of(item)))
                .andExpect(model().attribute("user", user));

        verify(orderService, times(1)).getUnfinishedOrder(orderNumber);
        verify(userAdapter, times(1)).getUserInfo();
    }

    @Test
    @DisplayName("비회원의 주문서 작성 페이지 응답에 성공하면 200을 응답한다")
    void orderPage_NonMemberUser_Success() throws Exception {
        // Given
        String orderNumber = "202507-abcdef-123456";
        CreateOrderResponse orderResponse = new CreateOrderResponse(orderNumber, List.of());

        given(orderService.getUnfinishedOrder(orderNumber)).willReturn(orderResponse);

        // When & Then
        mockMvc.perform(get("/orders/{orderNumber}/input-detail", orderNumber)
                        .flashAttr("isLoggedIn", false))
                .andExpect(status().isOk())
                .andExpect(view().name("order/non-member-order"))
                .andExpect(model().attribute("orderNumber", orderNumber))
                .andExpect(model().attribute("items", List.of()));

        verify(orderService).getUnfinishedOrder(orderNumber);
    }

    @Test
    @DisplayName("주문 생성에 성공하면 리다이렉트를 응답한다")
    void createOrder_Success() throws Exception {
        // Given
        CreateOrderRequest request = new CreateOrderRequest();
        CreateOrderRequest.CreateOrderItemRequest itemRequest = new CreateOrderRequest.CreateOrderItemRequest(1L, 1);
        request.setCreateItemRequests(List.of(itemRequest));
        CreateOrderResponse response = new CreateOrderResponse("202507-abcdef-123456", null);

        given(orderService.createOrder(any(CreateOrderRequest.class))).willReturn(response);

        // When & Then
        mockMvc.perform(post("/orders")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .flashAttr("createOrderRequest", request))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/orders/202507-abcdef-123456/input-detail"));

        verify(orderService).createOrder(any(CreateOrderRequest.class));
    }

    @Test
    @DisplayName("주문서 작성이 끝나고 버튼을 누르면 결제 페이지 리다이렉트를 응답한다")
    void updateOrder_Success() throws Exception {
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

        given(orderService.updateOrder(eq(orderNumber), any(UpdateOrderRequest.class))).willReturn(response);

        // When & Then
        mockMvc.perform(put("/orders/{orderNumber}", orderNumber)
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .flashAttr("updateOrderRequest", request))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/payments/form?orderId=" + orderNumber + "&amount=10000"));

        verify(orderService).updateOrder(eq(orderNumber), any(UpdateOrderRequest.class));
    }

    @Test
    @DisplayName("주문 목록 조회에 성공하면 200을 응답한다")
    void orderList_Success() throws Exception {
        // Given
        Pageable pageable = PageRequest.of(0, 10);
        OrderSummaryResponse summary = new OrderSummaryResponse(LocalDate.now(), "202507-abcdef-123456", "받는 사람", 30000L, "PENDING");
        Page<OrderSummaryResponse> orderPage = new PageImpl<>(List.of(summary), pageable, 1);

        given(orderService.getAllOrders(any(Pageable.class))).willReturn(orderPage);

        // When & Then
        mockMvc.perform(get("/orders/list")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(view().name("order/list"))
                .andExpect(model().attribute("orders", orderPage));

        verify(orderService).getAllOrders(any(Pageable.class));
    }

    @Test
    @DisplayName("주문 상세 조회에 성공하면 200을 응답한다")
    void getOrderDetail_Success() throws Exception {
        // Given
        String orderNumber = "202507-abcdef-123456";
        OrderDetailResponse orderDetail = OrderDetailResponse.builder()
                .orderNumber(orderNumber)
                .totalAmount(30_000L)
                .build();

        given(orderService.getOrder(orderNumber)).willReturn(orderDetail);

        // When & Then
        mockMvc.perform(get("/orders/list/{orderNumber}", orderNumber))
                .andExpect(status().isOk())
                .andExpect(view().name("order/detail"))
                .andExpect(model().attribute("order", orderDetail));

        verify(orderService).getOrder(orderNumber);
    }

    @Test
    @DisplayName("비회원 주문 조회에 성공하면 200을 응답한다")
    void nonMemberOrderDetail_Success() throws Exception {
        // Given
        String orderNumber = "202507-abcdef-123456";
        OrderDetailResponse orderDetail = OrderDetailResponse.builder()
                .orderNumber(orderNumber)
                .totalAmount(30_000L)
                .build();

        given(orderService.getOrder(orderNumber)).willReturn(orderDetail);

        // When & Then
        mockMvc.perform(get("/orders/non-member-detail")
                        .flashAttr("nonMemberOrderNumber", orderNumber))
                .andExpect(status().isOk())
                .andExpect(view().name("order/non-member-order-detail"))
                .andExpect(model().attribute("order", orderDetail));

        verify(orderService).getOrder(orderNumber);
    }

    @Test
    @DisplayName("비회원 주문 조회에 실패하면 로그인 화면으로 리다이렉트를 응답한다(이유: orderNumber 전달 안됨)")
    void nonMemberOrderDetail_NoOrderNumber_RedirectToLogin() throws Exception {
        // When & Then
        mockMvc.perform(get("/orders/non-member-detail"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/auth/login"))
                .andExpect(flash().attribute("nonMemberLoginError", "주문 정보를 찾을 수 없습니다."));
    }

    @Test
    @DisplayName("비회원 주문 조회에 실패하면 로그인 화면으로 리다이렉트를 응답한다(이유: 주문이 존재하지 않음)")
    void nonMemberOrderDetail_Exception_RedirectToLogin() throws Exception {
        // Given
        String orderNumber = "202507-abcdef-123456";
        given(orderService.getOrder(orderNumber)).willThrow(new OrderNotFoundException("주문 정보를 찾을 수 없습니다."));

        // When & Then
        mockMvc.perform(get("/orders/non-member-detail")
                        .flashAttr("nonMemberOrderNumber", orderNumber))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/auth/login"))
                .andExpect(flash().attribute("nonMemberLoginError", "주문 정보를 찾을 수 없습니다."));

        verify(orderService).getOrder(orderNumber);
    }
}