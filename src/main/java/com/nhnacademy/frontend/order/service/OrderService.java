package com.nhnacademy.frontend.order.service;

import com.nhnacademy.frontend.order.dto.request.OrderRequest;
import com.nhnacademy.frontend.order.dto.response.OrderResponse;
import jakarta.validation.Valid;

public interface OrderService {

    OrderResponse createOrder(@Valid OrderRequest orderRequest);
}