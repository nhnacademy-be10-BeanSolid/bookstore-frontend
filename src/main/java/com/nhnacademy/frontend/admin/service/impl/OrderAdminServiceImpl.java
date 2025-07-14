package com.nhnacademy.frontend.admin.service.impl;

import com.nhnacademy.frontend.admin.adapter.OrderAdminAdapter;
import com.nhnacademy.frontend.admin.service.OrderAdminService;
import com.nhnacademy.frontend.order.dto.response.OrderResponse;
import com.nhnacademy.frontend.order.dto.response.OrderSummaryResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OrderAdminServiceImpl implements OrderAdminService {

    private final OrderAdminAdapter adminAdapter;

    @Override
    public Page<OrderSummaryResponse> getAllOrders(Pageable pageable) {
        return adminAdapter.getAllOrders(pageable);
    }

    @Override
    public OrderResponse changeOrderStatus(String orderNumber) {
        return adminAdapter.changeOrderStatus(orderNumber);
    }
}
