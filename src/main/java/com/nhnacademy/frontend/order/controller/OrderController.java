package com.nhnacademy.frontend.order.controller;

import com.nhnacademy.frontend.order.dto.CartItem;
import com.nhnacademy.frontend.order.dto.request.OrderRequest;
import com.nhnacademy.frontend.order.dto.response.OrderResponse;
import com.nhnacademy.frontend.order.dto.response.OrderSummaryResponse;
import com.nhnacademy.frontend.order.service.OrderService;
import com.nhnacademy.frontend.user.exception.ValidationFailedException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
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

    @GetMapping
    public String orderPage(Model model) {
        //TODO: 장바구니 혹은 바로구매로 주문도서 정보 가져올 예정.
        CartItem cartItem1 = new CartItem(1L, "빈틈없조1", 1, 5_000L);
        CartItem cartItem2 = new CartItem(2L, "빈틈없조2", 1, 7_000L);
        model.addAttribute(List.of(cartItem1, cartItem2));

        return "order/order";
    }

    @PostMapping
    public String createOrder(@Valid @ModelAttribute OrderRequest orderRequest,
                                    BindingResult bindingResult,
                                    RedirectAttributes redirectAttributes) {
        log.info("POST /orders - 주문 생성 요청 [받는 사람: {}]", orderRequest.getReceiverName());

        if (bindingResult.hasErrors()) {
            throw new ValidationFailedException(bindingResult); //TODO: user꺼 가져다 썼는데 나중에 변경 예정.
        }

        try {
            OrderResponse orderResponse = orderService.createOrder(orderRequest);
            log.info("POST /orders - 성공 리다이렉트 [주문번호: {}]", orderResponse.orderId());

            redirectAttributes.addFlashAttribute("orderResponse", orderResponse);

            return "redirect:/orders/tempPay";
        } catch (Exception e) {
            log.warn("POST /orders - 실패 리다이렉트 [에러: {}]", e.getMessage(), e);
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());

            return "redirect:/orders";
        }
    }

    // 주문 전체 조회 페이지
    @GetMapping("/list")
    public String orderList(Model model) {
        Page<OrderSummaryResponse> orders = orderService.getAllOrders();
        model.addAttribute("orders", orders);

        return "order/list";
    }

    // 주문 상세 조회 페이지
    @GetMapping("/{orderId}")
    public String getOrderDetail(@PathVariable String orderId, Model model) {
        OrderResponse orderDetail = orderService.getOrder(orderId);
        model.addAttribute("order", orderDetail);

        return "order/detail";
    }

    // 임시 결제 단계 페이지
    @GetMapping("/tempPay")
    public String payPage() {
        return "order/tempPay";
    }
}