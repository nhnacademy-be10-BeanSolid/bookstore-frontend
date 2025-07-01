package com.nhnacademy.frontend.order.controller;

import com.nhnacademy.frontend.order.domain.OrderRequestDto;
import com.nhnacademy.frontend.order.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.servlet.http.HttpSession;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/order")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @GetMapping
    public String showOrderPage(Model model, HttpSession session) {
        List<OrderRequestDto.OrderItemDto> cartItems = getCartItemsFromSession(session);
        
        if (cartItems.isEmpty()) {
            return "redirect:/cart";
        }

        BigDecimal totalAmount = cartItems.stream()
            .map(OrderRequestDto.OrderItemDto::getTotalPrice)
            .reduce(BigDecimal.ZERO, BigDecimal::add);

        OrderRequestDto orderRequest = new OrderRequestDto();
        orderRequest.setOrderItems(cartItems);
        orderRequest.setTotalAmount(totalAmount);

        model.addAttribute("orderRequest", orderRequest);
        model.addAttribute("cartItems", cartItems);
        model.addAttribute("totalAmount", totalAmount);

        return "order/order";
    }

    @PostMapping
    public String processOrder(@ModelAttribute OrderRequestDto orderRequest,
                               BindingResult bindingResult,
                               RedirectAttributes redirectAttributes,
                               HttpSession session) {
        
        if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute("errorMessage", "입력 정보를 확인해주세요.");
            return "redirect:/order";
        }

        try {
            Long orderId = orderService.createOrder(orderRequest);
            session.removeAttribute("cartItems");
            
            redirectAttributes.addFlashAttribute("successMessage", "주문이 완료되었습니다.");
            redirectAttributes.addFlashAttribute("orderId", orderId);
            return "redirect:/order/success";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "주문 처리 중 오류가 발생했습니다.");
            return "redirect:/order/fail";
        }
    }

    @GetMapping("/complete")
    public String showOrderComplete(Model model) {
        return "order/complete";
    }

    private List<OrderRequestDto.OrderItemDto> getCartItemsFromSession(HttpSession session) {
        @SuppressWarnings("unchecked")
        List<OrderRequestDto.OrderItemDto> cartItems = 
            (List<OrderRequestDto.OrderItemDto>) session.getAttribute("cartItems");
        
        if (cartItems == null) {
            cartItems = createSampleCartItems();
            session.setAttribute("cartItems", cartItems);
        }
        
        return cartItems;
    }

    private List<OrderRequestDto.OrderItemDto> createSampleCartItems() {
        List<OrderRequestDto.OrderItemDto> items = new ArrayList<>();
        
        OrderRequestDto.OrderItemDto item1 = new OrderRequestDto.OrderItemDto();
        item1.setBookId(1L);
        item1.setBookTitle("Spring Boot 완벽 가이드");
        item1.setQuantity(1);
        item1.setPrice(new BigDecimal("25000"));
        item1.setTotalPrice(new BigDecimal("25000"));
        items.add(item1);
        
        OrderRequestDto.OrderItemDto item2 = new OrderRequestDto.OrderItemDto();
        item2.setBookId(2L);
        item2.setBookTitle("Java 프로그래밍 입문");
        item2.setQuantity(2);
        item2.setPrice(new BigDecimal("18000"));
        item2.setTotalPrice(new BigDecimal("36000"));
        items.add(item2);
        
        return items;
    }

    @GetMapping("/success")
    public String successView() {
        return "/order/success";
    }

    @GetMapping("/fail")
    public String failView() {
        return "/order/fail";
    }
}