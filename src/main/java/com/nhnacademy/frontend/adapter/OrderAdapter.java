package com.nhnacademy.frontend.adapter;

import com.nhnacademy.frontend.order.dto.request.OrderRequest;
import com.nhnacademy.frontend.order.dto.response.OrderResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

@FeignClient(name = "gateway-service", contextId = "orderAdapter")
public interface OrderAdapter {
    
    @PostMapping("/order-api/orders")
    OrderResponse createOrder(@RequestBody OrderRequest orderRequest);
}