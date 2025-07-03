package com.nhnacademy.frontend.adapter;

import com.nhnacademy.frontend.order.domain.OrderRequestDto;
import com.nhnacademy.frontend.order.domain.OrderResponseDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

@FeignClient(name = "gateway-service", contextId = "orderAdapter")
public interface OrderAdapter {
    
    @PostMapping("/order-api/orders")
    OrderResponseDto createOrder(@RequestBody OrderRequestDto orderRequest, @RequestHeader("X-USER-ID") String xUserId);
    
    @GetMapping("/order-api/orders/{orderId}")
    OrderResponseDto getOrder(@PathVariable("orderId") Long orderId);
}