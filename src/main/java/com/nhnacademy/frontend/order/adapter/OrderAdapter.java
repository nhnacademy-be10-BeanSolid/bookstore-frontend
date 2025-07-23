package com.nhnacademy.frontend.order.adapter;

import com.nhnacademy.frontend.order.dto.request.CreateOrderRequest;
import com.nhnacademy.frontend.order.dto.request.OrderStatusRequest;
import com.nhnacademy.frontend.order.dto.request.ReturnsRequest;
import com.nhnacademy.frontend.order.dto.request.UpdateOrderRequest;
import com.nhnacademy.frontend.order.dto.response.*;
import jakarta.validation.Valid;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

@FeignClient(name = "gateway-service", contextId = "orderAdapter")
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

    @GetMapping("/order-api/orders/{orderNumber}")
    OrderDetailResponse getOrder(@PathVariable String orderNumber);

    @PutMapping("/order-api/orders/{orderNumber}/status")
    void returnOrder(@PathVariable String orderNumber, @RequestBody ReturnsRequest request);

    @PutMapping("/order-api/orders/{orderNumber}/status")
    OrderStatusResult changeOrderStatus(@PathVariable String orderNumber,
                                                               @Valid @RequestBody OrderStatusRequest request);
}