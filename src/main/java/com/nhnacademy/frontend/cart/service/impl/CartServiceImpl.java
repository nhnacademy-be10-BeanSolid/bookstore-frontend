package com.nhnacademy.frontend.cart.service.impl;

import com.nhnacademy.frontend.cart.dto.request.CartUpdateRequest;
import com.nhnacademy.frontend.common.adapter.BookAdapter;
import com.nhnacademy.frontend.cart.adapter.CartAdapter;
import com.nhnacademy.frontend.cart.domain.OwnerType;
import com.nhnacademy.frontend.cart.dto.request.CartAddItemRequest;
import com.nhnacademy.frontend.cart.dto.response.BookResponse;
import com.nhnacademy.frontend.cart.dto.response.CartCreateResponse;
import com.nhnacademy.frontend.cart.dto.response.CartItemDto;
import com.nhnacademy.frontend.cart.dto.response.CartResponse;
import com.nhnacademy.frontend.cart.dto.view.CartItemViewModel;
import com.nhnacademy.frontend.cart.service.CartService;
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
        CartAndOwnerInfo cartAndOwnerInfo = getOrCreateCartAndOwnerInfo(isLoggedIn, guestUUID, response);
        CartResponse cartResponse = cartAndOwnerInfo.cartResponse();

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
        CartAndOwnerInfo cartAndOwnerInfo = getOrCreateCartAndOwnerInfo(isLoggedIn, guestUUID, response);
        boolean itemFound = false;
        for (CartItemDto item : cartAndOwnerInfo.cartResponse().getItems()) {
            if (item.getItemId() == bookId) {
                cartAdapter.updateItemQuantity(cartAndOwnerInfo.ownerType(), cartAndOwnerInfo.uuid(), bookId, new CartUpdateRequest(quantity));
                itemFound = true;
                break;
            }
        }

        if (!itemFound) {
            cartAdapter.addItemToCart(cartAndOwnerInfo.ownerType(), cartAndOwnerInfo.uuid(), new CartAddItemRequest(bookId, quantity));
        }
    }

    @Override
    public void deleteCartItems(List<Long> bookIds, boolean isLoggedIn, String guestUUID, HttpServletResponse response) {
        CartAndOwnerInfo cartAndOwnerInfo = getOrCreateCartAndOwnerInfo(isLoggedIn, guestUUID, response);
        cartAdapter.deleteItemsFromCart(cartAndOwnerInfo.ownerType(), cartAndOwnerInfo.uuid(), bookIds);
    }

    @Override
    public void updateCartItems(Map<Long, Integer> quantities, boolean isLoggedIn, String guestUUID, HttpServletResponse response) {
        CartAndOwnerInfo cartAndOwnerInfo = getOrCreateCartAndOwnerInfo(isLoggedIn, guestUUID, response);
        for (Map.Entry<Long, Integer> entry : quantities.entrySet()) {
            Long bookId = entry.getKey();
            Integer quantity = entry.getValue();
            cartAdapter.updateItemQuantity(cartAndOwnerInfo.ownerType(), cartAndOwnerInfo.uuid(), bookId, new CartUpdateRequest(quantity));
        }
    }

    private record CartAndOwnerInfo(CartResponse cartResponse, OwnerType ownerType, String uuid) {}

    private CartAndOwnerInfo getOrCreateCartAndOwnerInfo(boolean isLoggedIn, String guestUUID, HttpServletResponse response) {
        OwnerType ownerType = isLoggedIn ? OwnerType.USER : OwnerType.GUEST;
        String effectiveUuid = guestUUID;
        CartResponse cartResponse;

        try {
            cartResponse = cartAdapter.getCart(ownerType, effectiveUuid);
        } catch (FeignException.NotFound e) {
            CartCreateResponse create = cartAdapter.createCart(ownerType);
            if (!isLoggedIn) {
                effectiveUuid = create.getGuestUUID();
                Cookie cookie = new Cookie("guest_uuid", effectiveUuid);
                cookie.setHttpOnly(true);
                cookie.setPath("/");
                cookie.setMaxAge(60 * 60 * 24 * 30);
                response.addCookie(cookie);
            }
            cartResponse = cartAdapter.getCart(ownerType, effectiveUuid);
        }
        return new CartAndOwnerInfo(cartResponse, ownerType, effectiveUuid);
    }
}
