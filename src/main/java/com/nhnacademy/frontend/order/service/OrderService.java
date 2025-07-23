package com.nhnacademy.frontend.order.service;

import com.nhnacademy.frontend.order.dto.request.CreateOrderRequest;
import com.nhnacademy.frontend.order.dto.request.UpdateOrderRequest;
import com.nhnacademy.frontend.order.dto.response.CreateOrderResponse;
import com.nhnacademy.frontend.order.dto.response.OrderDetailResponse;
import com.nhnacademy.frontend.order.dto.response.OrderResponse;

public interface OrderService {

    CreateOrderResponse createOrder(CreateOrderRequest request);
    CreateOrderResponse getUnfinishedOrder(String orderNumber);
    OrderResponse updateOrder(String orderNumber, UpdateOrderRequest orderRequest);
    OrderDetailResponse getOrder(String orderNumber);
}