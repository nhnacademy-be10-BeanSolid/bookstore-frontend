package com.nhnacademy.frontend.order.controller;

import com.nhnacademy.frontend.common.adapter.UserAdapter;
import com.nhnacademy.frontend.common.adapter.dto.user.response.ResponseUser;
import com.nhnacademy.frontend.common.exception.ValidationFailedException;
import com.nhnacademy.frontend.order.dto.request.CreateOrderRequest;
import com.nhnacademy.frontend.order.dto.request.UpdateOrderRequest;
import com.nhnacademy.frontend.order.dto.response.CreateOrderResponse;
import com.nhnacademy.frontend.order.dto.response.OrderDetailResponse;
import com.nhnacademy.frontend.order.dto.response.OrderResponse;
import com.nhnacademy.frontend.order.service.OrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
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

    private static final String ORDER = "order";

    private final OrderService orderService;
    private final UserAdapter userAdapter;

    @GetMapping("/{orderNumber}/input-detail")
    public String orderPage(@PathVariable String orderNumber,
                            Model model,
                            @ModelAttribute("isLoggedIn") boolean isLoggedIn) {
        CreateOrderResponse unfinishedOrder = orderService.getUnfinishedOrder(orderNumber);

        List<CreateOrderResponse.CreateOrderItemResponse> items = unfinishedOrder.getOrderItems();
        if (items == null) {
            items = List.of();
        }
        model.addAttribute("orderNumber", orderNumber);
        model.addAttribute("items", items);

        if (isLoggedIn) {
            try {
                ResponseEntity<ResponseUser> userResponse = userAdapter.getUserInfo();
                if (userResponse.getBody() != null) {
                    model.addAttribute("user", userResponse.getBody());
                }
            } catch (Exception e) {
                log.warn("회원 정보를 불러오는데 실패했습니다: {}", e.getMessage());
            }
            return "order/order";
        } else {
            return "order/non-member-order";
        }
    }

    @PostMapping
    public String createOrder(@Valid @ModelAttribute CreateOrderRequest request,
                              BindingResult bindingResult,
                              RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            throw new ValidationFailedException(bindingResult);
        }

        CreateOrderResponse order = orderService.createOrder(request);
        redirectAttributes.addFlashAttribute(ORDER, order);

        return "redirect:/orders/" + order.getOrderNumber() + "/input-detail";
    }

    @PutMapping("/{orderNumber}")
    public String updateOrder(@Valid @ModelAttribute UpdateOrderRequest request,
                              BindingResult bindingResult,
                              @PathVariable String orderNumber,
                              RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            throw new ValidationFailedException(bindingResult);
        }

        OrderResponse orderResponse = orderService.updateOrder(orderNumber, request);
        redirectAttributes.addFlashAttribute("orderResponse", orderResponse);
        return "redirect:/payments/form";
    }

    @GetMapping("/non-member-detail")
    public String nonMemberOrderDetail(Model model,
                                       RedirectAttributes redirectAttributes) {
        String orderNumber = (String) model.getAttribute("nonMemberOrderNumber");
        if (orderNumber == null || orderNumber.isEmpty()) {
            redirectAttributes.addFlashAttribute("nonMemberLoginError", "주문 정보를 찾을 수 없습니다.");
            return "redirect:/auth/login";
        }

        try {
            OrderDetailResponse orderDetail = orderService.getOrder(orderNumber);
            model.addAttribute(ORDER, orderDetail);
            return "order/non-member-order-detail";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("nonMemberLoginError", "주문 정보를 찾을 수 없습니다.");
            return "redirect:/auth/login";
        }
    }
}