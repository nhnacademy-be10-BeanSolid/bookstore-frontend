package com.nhnacademy.frontend.order.service.impl;

import com.nhnacademy.frontend.order.adapter.OrderAdapter;
import com.nhnacademy.frontend.order.dto.request.CreateOrderRequest;
import com.nhnacademy.frontend.order.dto.request.UpdateOrderRequest;
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
    public CreateOrderResponse getUnfinishedOrder(String orderNumber) {
        return orderAdapter.getUnfinishedOrder(orderNumber);
    }

    @Override
    public OrderResponse updateOrder(String orderNumber, UpdateOrderRequest orderRequest) {
        return orderAdapter.updateOrder(orderNumber, orderRequest);
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
