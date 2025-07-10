package com.nhnacademy.frontend.common.adapter;

import com.nhnacademy.frontend.order.dto.request.OrderRequest;
import com.nhnacademy.frontend.order.dto.response.OrderResponse;
import com.nhnacademy.frontend.order.dto.response.OrderSummaryResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

@FeignClient(name = "gateway-service", contextId = "orderAdapter1")
public interface OrderAdapter {
    
    @PostMapping("/order-api/orders")
    OrderResponse createOrder(@RequestBody OrderRequest orderRequest);

    @GetMapping("/order-api/orders")
    Page<OrderSummaryResponse> getAllOrdersByUserId();

    @GetMapping("/order-api/orders/{orderId}")
    OrderResponse getOrder(@PathVariable String orderId);
}