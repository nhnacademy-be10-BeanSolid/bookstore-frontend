package com.nhnacademy.frontend.cart.controller;

import com.nhnacademy.frontend.cart.dto.GuestUuidProvider;
import com.nhnacademy.frontend.cart.dto.request.CartItemUpdateRequest;
import java.util.stream.Collectors;

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

    private static final String REDIRECT_CART = "redirect:/cart";

    private final CartService cartService;

    @GetMapping
    public String cartForm(Model model,
                           @ModelAttribute("isLoggedIn") boolean isLoggedIn,
                           @CookieValue(value = "guest_uuid", required = false) String guestUUID,
                           HttpServletResponse response) {
        CartViewResponse cartViewResponse = cartService.getCartItems(isLoggedIn, guestUUID);
        handleGuestCookie(cartViewResponse, response);

        List<CartItemUpdateRequest> updates = cartViewResponse.cartItems().stream()
                .map(item -> new CartItemUpdateRequest(item.getBookId(), item.getQuantity()))
                .collect(Collectors.toList());

        CartUpdateQuantitiesRequest cartUpdateQuantitiesRequest = new CartUpdateQuantitiesRequest();
        cartUpdateQuantitiesRequest.setUpdates(updates);

        model.addAttribute("cartItems", cartViewResponse.cartItems());
        model.addAttribute("cartUpdateQuantitiesRequest", cartUpdateQuantitiesRequest);
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
        return REDIRECT_CART;
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
        return REDIRECT_CART;
    }

    @PostMapping("/update")
    public String updateCart(@ModelAttribute CartUpdateQuantitiesRequest request,
                             @ModelAttribute("isLoggedIn") boolean isLoggedIn,
                             @CookieValue(value = "guest_uuid", required = false) String guestUUID,
                             HttpServletResponse response) {
        CartOperationResult result = cartService.updateCartItems(request.getUpdates(), isLoggedIn, guestUUID);
        handleGuestCookie(result, response);
        return REDIRECT_CART;
    }

    private void handleGuestCookie(GuestUuidProvider provider, HttpServletResponse response) {
        if (provider.newGuestUuid() != null) {
            addGuestCookie(response, provider.newGuestUuid());
        }
    }

    private void addGuestCookie(HttpServletResponse response, String guestUUID) {
        Cookie cookie = new Cookie("guest_uuid", guestUUID);
        cookie.setHttpOnly(true);
        cookie.setPath("/");
        cookie.setMaxAge(60 * 60 * 24 * 30); // 30 days
        response.addCookie(cookie);
    }
}
