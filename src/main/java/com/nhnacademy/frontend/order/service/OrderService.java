package com.nhnacademy.frontend.order.service;

import com.nhnacademy.frontend.order.dto.request.CreateOrderRequest;
import com.nhnacademy.frontend.order.dto.request.UpdateOrderRequest;
import com.nhnacademy.frontend.order.dto.response.CreateOrderResponse;
import com.nhnacademy.frontend.order.dto.response.OrderDetailResponse;
import com.nhnacademy.frontend.order.dto.response.OrderResponse;
import com.nhnacademy.frontend.order.dto.response.OrderSummaryResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface OrderService {

    CreateOrderResponse createOrder(CreateOrderRequest request);
    CreateOrderResponse getUnfinishedOrder(String orderNumber);
    OrderResponse updateOrder(String orderNumber, UpdateOrderRequest orderRequest);
    Page<OrderSummaryResponse> getAllOrders(Pageable pageable);
    OrderDetailResponse getOrder(String orderNumber);
}