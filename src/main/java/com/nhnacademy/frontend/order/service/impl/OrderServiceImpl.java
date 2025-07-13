package com.nhnacademy.frontend.order.service.impl;

import com.nhnacademy.frontend.order.adapter.OrderAdapter;
import com.nhnacademy.frontend.order.dto.request.CreateOrderRequest;
import com.nhnacademy.frontend.order.dto.request.OrderRequest;
import com.nhnacademy.frontend.order.dto.response.CreateOrderResponse;
import com.nhnacademy.frontend.order.dto.response.OrderDetailResponse;
import com.nhnacademy.frontend.order.dto.response.OrderResponse;
import com.nhnacademy.frontend.order.dto.response.OrderSummaryResponse;
import com.nhnacademy.frontend.order.service.OrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final OrderAdapter orderAdapter;

    @Override
    public CreateOrderResponse createOrder(CreateOrderRequest request) {
        return orderAdapter.createOrder(request);
    }

    @Override
    public OrderResponse updateOrder(OrderRequest orderRequest) {
        log.debug("주문 생성 요청 - 받는 사람: {}", orderRequest.getReceiverName());

        OrderResponse orderResponse = orderAdapter.updateOrder(orderRequest); //TODO: feignclient 실패 처리 필요.

        log.debug("주문 생성 성공 - 주문번호: {}", orderResponse.orderId());

        return orderResponse;
    }

    @Override
    public Page<OrderSummaryResponse> getAllOrders(Pageable pageable) {
        return orderAdapter.getAllOrdersByUserId(pageable);
    }

    @Override
    public OrderDetailResponse getOrder(String orderId) {
        return orderAdapter.getOrder(orderId);
    }
}
