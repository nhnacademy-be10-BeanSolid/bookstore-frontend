package com.nhnacademy.frontend.admin.service;

import com.nhnacademy.frontend.order.dto.response.OrderResponse;
import com.nhnacademy.frontend.order.dto.response.OrderSummaryResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface OrderAdminService {

    Page<OrderSummaryResponse> getAllOrders(Pageable pageable);
    OrderResponse changeOrderStatus(String orderNumber);
}
