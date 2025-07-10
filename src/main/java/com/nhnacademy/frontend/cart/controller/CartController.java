package com.nhnacademy.frontend.cart.controller;

import com.nhnacademy.frontend.cart.service.CartService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/cart")
public class CartController {

    private final CartService cartService;

    @GetMapping
    public String cartForm(Model model,
                           @ModelAttribute("isLoggedIn") boolean isLoggedIn,
                           @CookieValue(value = "guest_uuid", required = false) String guestUUID,
                           HttpServletResponse response) {
        model.addAttribute("cartItems", cartService.getCartItems(isLoggedIn, guestUUID, response));
        return "cart/cartForm";
    }

    @PostMapping("/add")
    public String addToCart(@RequestParam Long bookId, @RequestParam int quantity,
                            @ModelAttribute("isLoggedIn") boolean isLoggedIn,
                            @CookieValue(value = "guest_uuid", required = false) String guestUUID,
                            HttpServletResponse response) {
        cartService.addToCart(bookId, quantity, isLoggedIn, guestUUID, response);
        return "redirect:/cart";
    }

    @PostMapping("/delete")
    public String deleteFromCart(@RequestParam(name = "bookId") List<Long> bookIds,
                                 @ModelAttribute("isLoggedIn") boolean isLoggedIn,
                                 @CookieValue(value = "guest_uuid", required = false) String guestUUID,
                                 HttpServletResponse response) {
        cartService.deleteCartItems(bookIds, isLoggedIn, guestUUID, response);
        return "redirect:/cart";
    }
}
