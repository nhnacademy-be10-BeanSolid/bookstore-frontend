package com.nhnacademy.frontend.order.service;

import com.nhnacademy.frontend.order.dto.request.OrderRequest;
import com.nhnacademy.frontend.order.dto.response.OrderDetailResponse;
import com.nhnacademy.frontend.order.dto.response.OrderResponse;
import com.nhnacademy.frontend.order.dto.response.OrderSummaryResponse;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface OrderService {

    OrderResponse createOrder(OrderRequest orderRequest);
    Page<OrderSummaryResponse> getAllOrders(Pageable pageable);
    OrderDetailResponse getOrder(String orderId);
}