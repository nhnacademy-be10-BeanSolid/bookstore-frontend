package com.nhnacademy.frontend.admin.controller;

import com.nhnacademy.frontend.admin.service.OrderAdminService;
import com.nhnacademy.frontend.order.dto.response.OrderSummaryResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/admin/orders")
@RequiredArgsConstructor
public class OrderAdminController {

    private final OrderAdminService orderAdminService;

    @GetMapping
    public String orderList(Pageable pageable, Model model) {
        Page<OrderSummaryResponse> orders = orderAdminService.getAllOrders(pageable);
        model.addAttribute("orders", orders);

        return "admin/order/list";
    }

    @PutMapping("/{orderNumber}/status")
    public String changeStatusToShipping(@PathVariable String orderNumber) {
        orderAdminService.changeOrderStatus(orderNumber);
        return "redirect:/admin/orders";
    }
}
