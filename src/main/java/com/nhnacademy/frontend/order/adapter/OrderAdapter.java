package com.nhnacademy.frontend.order.adapter;

import com.nhnacademy.frontend.order.dto.request.CreateOrderRequest;
import com.nhnacademy.frontend.order.dto.request.UpdateOrderRequest;
import com.nhnacademy.frontend.order.dto.response.CreateOrderResponse;
import com.nhnacademy.frontend.order.dto.response.OrderDetailResponse;
import com.nhnacademy.frontend.order.dto.response.OrderResponse;
import com.nhnacademy.frontend.order.dto.response.OrderSummaryResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

@FeignClient(name = "gateway-service", contextId = "orderAdapter1")
public interface OrderAdapter {
    
    @PostMapping("/order-api/orders")
    CreateOrderResponse createOrder(@RequestBody CreateOrderRequest orderRequest);

    @GetMapping("/order-api/orders/{orderNumber}/input-detail")
    CreateOrderResponse getUnfinishedOrder(@PathVariable String orderNumber);

    @PutMapping("/order-api/orders/{orderNumber}")
    OrderResponse updateOrder(@PathVariable String orderNumber,
                              @RequestBody UpdateOrderRequest orderRequest);

    @GetMapping("/order-api/orders")
    Page<OrderSummaryResponse> getAllOrdersByUserId(Pageable pageable);

    @GetMapping("/order-api/orders/{orderId}")
    OrderDetailResponse getOrder(@PathVariable String orderId);
}