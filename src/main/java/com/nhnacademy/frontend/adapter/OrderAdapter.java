package com.nhnacademy.frontend.adapter;

import com.nhnacademy.frontend.order.domain.OrderRequestDto;
import com.nhnacademy.frontend.order.domain.OrderResponseDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "gateway-service", contextId = "orderAdapter")
public interface OrderAdapter {
    
    @PostMapping("/order-service/orders")
    OrderResponseDto createOrder(@RequestBody OrderRequestDto orderRequest);
    
    @GetMapping("/order-service/orders/{orderId}")
    OrderResponseDto getOrder(@PathVariable("orderId") Long orderId);
}