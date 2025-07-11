package com.nhnacademy.frontend.cart.controller;

import com.nhnacademy.frontend.cart.dto.CartOperationResult;
import com.nhnacademy.frontend.cart.dto.CartViewResponse;
import com.nhnacademy.frontend.cart.dto.request.CartUpdateQuantitiesRequest;
import com.nhnacademy.frontend.cart.service.CartService;
import jakarta.servlet.http.Cookie;
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
        CartViewResponse cartViewResponse = cartService.getCartItems(isLoggedIn, guestUUID);
        if (cartViewResponse.newGuestUuid() != null) {
            addGuestCookie(response, cartViewResponse.newGuestUuid());
        }
        model.addAttribute("cartItems", cartViewResponse.cartItems());
        return "cart/cartForm";
    }

    @PostMapping("/add")
    public String addToCart(@RequestParam Long bookId, @RequestParam int quantity,
                            @ModelAttribute("isLoggedIn") boolean isLoggedIn,
                            @CookieValue(value = "guest_uuid", required = false) String guestUUID,
                            HttpServletResponse response) {
        CartOperationResult result = cartService.addToCart(bookId, quantity, isLoggedIn, guestUUID);
        if (result.newGuestUuid() != null) {
            addGuestCookie(response, result.newGuestUuid());
        }
        return "redirect:/cart";
    }

    @PostMapping("/delete")
    public String deleteFromCart(@RequestParam(name = "bookId") List<Long> bookIds,
                                 @ModelAttribute("isLoggedIn") boolean isLoggedIn,
                                 @CookieValue(value = "guest_uuid", required = false) String guestUUID,
                                 HttpServletResponse response) {
        CartOperationResult result = cartService.deleteCartItems(bookIds, isLoggedIn, guestUUID);
        if (result.newGuestUuid() != null) {
            addGuestCookie(response, result.newGuestUuid());
        }
        return "redirect:/cart";
    }

    @PostMapping("/update")
    public String updateCart(@ModelAttribute CartUpdateQuantitiesRequest request,
                             @ModelAttribute("isLoggedIn") boolean isLoggedIn,
                             @CookieValue(value = "guest_uuid", required = false) String guestUUID,
                             HttpServletResponse response) {
        CartOperationResult result = cartService.updateCartItems(request.getQuantities(), isLoggedIn, guestUUID);
        if (result.newGuestUuid() != null) {
            addGuestCookie(response, result.newGuestUuid());
        }
        return "redirect:/cart";
    }

    private void addGuestCookie(HttpServletResponse response, String guestUUID) {
        Cookie cookie = new Cookie("guest_uuid", guestUUID);
        cookie.setHttpOnly(true);
        cookie.setPath("/");
        cookie.setMaxAge(60 * 60 * 24 * 30); // 30 days
        response.addCookie(cookie);
    }
}
