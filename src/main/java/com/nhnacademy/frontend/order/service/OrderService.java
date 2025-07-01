package com.nhnacademy.frontend.order.service;

import com.nhnacademy.frontend.order.domain.OrderRequestDto;
import com.nhnacademy.frontend.order.domain.OrderResponseDto;

public interface OrderService {
    Long createOrder(OrderRequestDto orderRequest);
    OrderResponseDto getOrder(Long orderId);
}