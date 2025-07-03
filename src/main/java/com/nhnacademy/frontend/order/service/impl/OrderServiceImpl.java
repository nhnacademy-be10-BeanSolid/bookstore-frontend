package com.nhnacademy.frontend.order.service.impl;

import com.nhnacademy.frontend.order.domain.OrderRequestDto;
import com.nhnacademy.frontend.order.domain.OrderResponseDto;
import com.nhnacademy.frontend.order.service.OrderService;
import com.nhnacademy.frontend.adapter.OrderAdapter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final OrderAdapter orderAdapter;

    @Override
    public Long createOrder(OrderRequestDto orderRequest) {
        OrderResponseDto response = orderAdapter.createOrder(orderRequest, "test");
        return response.getOrderId();
    }

    @Override
    public OrderResponseDto getOrder(Long orderId) {
        return orderAdapter.getOrder(orderId);
    }
}