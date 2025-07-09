package com.nhnacademy.frontend.order.service;

import com.nhnacademy.frontend.order.dto.request.OrderRequest;
import com.nhnacademy.frontend.order.dto.response.OrderResponse;
import com.nhnacademy.frontend.order.dto.response.OrderSummaryResponse;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;

public interface OrderService {

    OrderResponse createOrder(@Valid OrderRequest orderRequest);
    Page<OrderSummaryResponse> getAllOrders();
    OrderResponse getOrder(String orderId);
}