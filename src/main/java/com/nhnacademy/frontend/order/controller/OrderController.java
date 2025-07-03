package com.nhnacademy.frontend.order.controller;

import com.nhnacademy.frontend.order.domain.OrderRequestDto;
import com.nhnacademy.frontend.order.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @GetMapping
    public String showOrderPage(Model model) {
        // 샘플 장바구니 아이템 생성 (실제로는 장바구니 서비스에서 가져올 예정)
        List<OrderRequestDto.OrderItemDto> cartItems = createSampleCartItems();
        
        if (cartItems.isEmpty()) {
            return "redirect:/cart";
        }

        Long totalAmount = cartItems.stream()
            .map(item -> item.getPrice() * item.getQuantity())
            .reduce(0L, Long::sum);

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
                               BindingResult bindingResult) {
        
        if (bindingResult.hasErrors()) {
            return "redirect:/orders";
        }

        try {
            Long orderId = orderService.createOrder(orderRequest);

            return "redirect:/orders/success";
        } catch (Exception e) {
            return "redirect:/orders/fail";
        }
    }
    
    @PostMapping(consumes = "application/json")
    @ResponseBody
    public Map<String, Object> processOrderJson(@RequestBody OrderRequestDto orderRequest) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            Long orderId = orderService.createOrder(orderRequest);
            
            response.put("success", true);
            response.put("orderId", orderId);
            response.put("message", "주문이 성공적으로 처리되었습니다.");
            
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "주문 처리 중 오류가 발생했습니다: " + e.getMessage());
        }
        
        return response;
    }

    @GetMapping("/complete")
    public String showOrderComplete(Model model) {
        return "order/complete";
    }


    private List<OrderRequestDto.OrderItemDto> createSampleCartItems() {
        List<OrderRequestDto.OrderItemDto> items = new ArrayList<>();
        
        OrderRequestDto.OrderItemDto item1 = new OrderRequestDto.OrderItemDto();
        item1.setBookId(1L);
        item1.setBookTitle("Spring Boot 완벽 가이드");
        item1.setQuantity(1);
        item1.setPrice(25000L);
        items.add(item1);
        
        OrderRequestDto.OrderItemDto item2 = new OrderRequestDto.OrderItemDto();
        item2.setBookId(2L);
        item2.setBookTitle("Java 프로그래밍 입문");
        item2.setQuantity(2);
        item2.setPrice(18000L);
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