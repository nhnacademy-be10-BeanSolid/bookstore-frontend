package com.nhnacademy.frontend.order.controller;

import com.nhnacademy.frontend.common.exception.ValidationFailedException;
import com.nhnacademy.frontend.order.dto.request.CreateOrderRequest;
import com.nhnacademy.frontend.order.dto.request.OrderRequest;
import com.nhnacademy.frontend.order.dto.response.CreateOrderResponse;
import com.nhnacademy.frontend.order.dto.response.OrderDetailResponse;
import com.nhnacademy.frontend.order.dto.response.OrderResponse;
import com.nhnacademy.frontend.order.dto.response.OrderSummaryResponse;
import com.nhnacademy.frontend.order.exception.OrderNotFoundException;
import com.nhnacademy.frontend.order.service.OrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Slf4j
@Controller
@RequestMapping("/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @GetMapping("/{orderNumber}/input-detail")
    public String orderPage(@PathVariable String orderNumber,
                            Model model) {
        CreateOrderResponse order = (CreateOrderResponse) model.getAttribute("order");
        if (order == null) {
            throw new OrderNotFoundException("주문 정보를 찾을 수 없습니다.");
        }
        
        List<CreateOrderResponse.CreateOrderItemResponse> items = order.getOrderItems();
        if (items == null) {
            items = List.of();
        }
        model.addAttribute("orderNumber", orderNumber);
        model.addAttribute("items", items);

        return "order/order";
    }

    @PostMapping
    public String createOrder(@Valid @ModelAttribute CreateOrderRequest request,
                              BindingResult bindingResult,
                              RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            throw new ValidationFailedException(bindingResult);
        }

        CreateOrderResponse order = orderService.createOrder(request);
        redirectAttributes.addFlashAttribute("order", order);

        return "redirect:/orders/" + order.getOrderNumber() + "/input-detail";
    }

    @PostMapping("/{orderId}")
    public String updateOrder(@Valid @ModelAttribute OrderRequest orderRequest,
                              BindingResult bindingResult,
                              RedirectAttributes redirectAttributes) {
        log.info("POST /orders - 주문 생성 요청 [받는 사람: {}]", orderRequest.getReceiverName());

        if (bindingResult.hasErrors()) {
            throw new ValidationFailedException(bindingResult);
        }

        try {
            OrderResponse orderResponse = orderService.updateOrder(orderRequest);
            log.debug("POST /orders - 성공 리다이렉트 [주문번호: {}]", orderResponse.orderId());

            return "redirect:/payments/form?orderId=" + orderResponse.orderId() + "&amount=" + orderResponse.totalAmount();
        } catch (Exception e) {
            log.warn("POST /orders - 실패 리다이렉트 [에러: {}]", e.getMessage(), e);
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());

            return "redirect:/orders";
        }
    }

    // 주문 전체 조회 페이지
    @GetMapping("/list")
    public String orderList(Pageable pageable, Model model) {
        Page<OrderSummaryResponse> orders = orderService.getAllOrders(pageable);
        model.addAttribute("orders", orders);

        return "order/list";
    }

    // 주문 상세 조회 페이지
    @GetMapping("/list/{orderId}")
    public String getOrderDetail(@PathVariable String orderId, Model model) {
        OrderDetailResponse orderDetail = orderService.getOrder(orderId);
        model.addAttribute("order", orderDetail);

        return "order/detail";
    }
}