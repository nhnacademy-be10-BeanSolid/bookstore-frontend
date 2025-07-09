package com.nhnacademy.frontend.cart.service.impl;

import com.nhnacademy.frontend.cart.adapter.CartAdapter;
import com.nhnacademy.frontend.cart.domain.OwnerType;
import com.nhnacademy.frontend.cart.dto.request.CartAddItemRequest;
import com.nhnacademy.frontend.cart.dto.response.BookResponse;
import com.nhnacademy.frontend.cart.dto.response.CartCreateResponse;
import com.nhnacademy.frontend.cart.dto.response.CartItemDto;
import com.nhnacademy.frontend.cart.dto.response.CartResponse;
import com.nhnacademy.frontend.cart.dto.view.CartItemViewModel;
import com.nhnacademy.frontend.cart.service.CartService;
import com.nhnacademy.frontend.common.adapter.BookAdapter;
import feign.FeignException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CartServiceImpl implements CartService {

    private final CartAdapter cartAdapter;
    private final BookAdapter bookAdapter;

    @Override
    public List<CartItemViewModel> getCartItems(boolean isLoggedIn, String guestUUID, HttpServletResponse response) {
        CartResponse cartResponse;
        if (isLoggedIn) {
            try {
                cartResponse = cartAdapter.getCart(OwnerType.USER, null);
            } catch (FeignException.NotFound e) {
                CartCreateResponse create = cartAdapter.createCart(OwnerType.USER);
                cartResponse = cartAdapter.getCart(OwnerType.USER, null); // Retry to get the newly created cart
            }
        } else {
            try {
                cartResponse = cartAdapter.getCart(OwnerType.GUEST, guestUUID);
            } catch (FeignException.NotFound e) {
                CartCreateResponse create = cartAdapter.createCart(OwnerType.GUEST);
                Cookie cookie = new Cookie("guest_uuid", create.getGuestUUID());
                cookie.setHttpOnly(true);
                cookie.setPath("/");
                cookie.setMaxAge(60 * 60 * 24 * 30);
                response.addCookie(cookie);

                cartResponse = cartAdapter.getCart(OwnerType.GUEST, create.getGuestUUID());
            }
        }

        List<BookResponse> bookResponseList = bookAdapter.getBooks(cartResponse.getItems().stream()
                .map(CartItemDto::getItemId)
                .toList());


        Map<Long, Integer> itemQuantityMap = cartResponse.getItems().stream()
                .collect(Collectors.toMap(CartItemDto::getItemId, CartItemDto::getQuantity));

        return bookResponseList.stream()
                .map(item -> new CartItemViewModel(item.id(), item.title(), itemQuantityMap.get(item.id()), item.originalPrice()))
                .collect(Collectors.toList());
    }

    @Override
    public void addToCart(Long bookId, int quantity, boolean isLoggedIn, String guestUUID, HttpServletResponse response) {
        if(isLoggedIn) {
            try {
                cartAdapter.addItemToCart(OwnerType.USER, null, new CartAddItemRequest(bookId, quantity));
            } catch (FeignException.NotFound e) {
                CartCreateResponse create = cartAdapter.createCart(OwnerType.USER);
                cartAdapter.addItemToCart(OwnerType.USER, null, new CartAddItemRequest(bookId, quantity));
            }
        } else {
            try {
                cartAdapter.addItemToCart(OwnerType.GUEST, guestUUID, new CartAddItemRequest(bookId, quantity));
            } catch (FeignException.NotFound e) {
                CartCreateResponse create = cartAdapter.createCart(OwnerType.GUEST);

                Cookie cookie = new Cookie("guest_uuid", create.getGuestUUID());
                cookie.setHttpOnly(true);
                cookie.setPath("/");
                cookie.setMaxAge(60 * 60 * 24 * 30);
                response.addCookie(cookie);

                cartAdapter.addItemToCart(OwnerType.GUEST, create.getGuestUUID(), new CartAddItemRequest(bookId, quantity));
            }

        }
    }

    @Override
    public void deleteCartItems(List<Long> bookIds, boolean isLoggedIn, String guestUUID) {
        if(isLoggedIn) {
            cartAdapter.deleteItemsFromCart(OwnerType.USER, null, bookIds);
        } else {
            cartAdapter.deleteItemsFromCart(OwnerType.GUEST, guestUUID, bookIds);
        }
    }
}
