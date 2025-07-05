package com.nhnacademy.frontend.order.service.impl;

import com.nhnacademy.frontend.adapter.OrderAdapter;
import com.nhnacademy.frontend.order.dto.request.OrderRequest;
import com.nhnacademy.frontend.order.dto.response.OrderResponse;
import com.nhnacademy.frontend.order.service.OrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final OrderAdapter orderAdapter;

    @Override
    public OrderResponse createOrder(OrderRequest orderRequest) {
        log.info("주문 생성 요청 - 받는 사람: {}", orderRequest.getReceiverName());

        OrderResponse orderResponse = orderAdapter.createOrder(orderRequest); //TODO: feignclient 실패 처리 필요.

        log.info("주문 생성 성공 - 주문번호: {}", orderResponse.orderNumber());

        return orderResponse;
    }
}