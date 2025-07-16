package com.nhnacademy.frontend.admin.adapter;

import com.nhnacademy.frontend.order.dto.response.OrderResponse;
import com.nhnacademy.frontend.order.dto.response.OrderSummaryResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;

@FeignClient(name = "gateway-service", contextId = "orderAdminAdapter")
public interface OrderAdminAdapter {

    @GetMapping("/order-api/admin/orders")
    Page<OrderSummaryResponse> getAllOrders(Pageable pageable);

    @PutMapping("/order-api/admin/orders/{orderNumber}/status")
    OrderResponse changeOrderStatus(@PathVariable String orderNumber);
}
