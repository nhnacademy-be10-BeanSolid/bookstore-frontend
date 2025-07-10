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

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Controller
@RequestMapping("/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @GetMapping
    public String orderPage(Model model) {
        Long bookId = (Long) model.getAttribute("bookId");
        String title = (String) model.getAttribute("title");
        Integer salePrice = (Integer) model.getAttribute("salePrice");
        Boolean wrappable = (Boolean) model.getAttribute("wrappable");
        Integer quantity = (Integer) model.getAttribute("quantity");

        List<CartItem> items = new ArrayList<>();
        if (bookId != null && title != null && salePrice != null && quantity != null) {
            CartItem item = new CartItem(bookId, title, quantity, salePrice.longValue(), wrappable);
            items.add(item);
        }

        model.addAttribute("items", items);
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

            return "redirect:/payments/form?orderId=" + orderResponse.orderId() + "&amount=" + orderResponse.totalAmount();
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
}