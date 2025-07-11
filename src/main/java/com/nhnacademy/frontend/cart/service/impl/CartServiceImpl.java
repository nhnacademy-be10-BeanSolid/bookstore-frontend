package com.nhnacademy.frontend.cart.service.impl;

import com.nhnacademy.frontend.cart.adapter.CartAdapter;
import com.nhnacademy.frontend.cart.domain.OwnerType;
import com.nhnacademy.frontend.cart.dto.CartOperationResult;
import com.nhnacademy.frontend.cart.dto.CartViewResponse;
import com.nhnacademy.frontend.cart.dto.request.CartAddItemRequest;
import com.nhnacademy.frontend.cart.dto.request.CartUpdateRequest;
import com.nhnacademy.frontend.cart.dto.response.BookResponse;
import com.nhnacademy.frontend.cart.dto.response.CartCreateResponse;
import com.nhnacademy.frontend.cart.dto.response.CartItemDto;
import com.nhnacademy.frontend.cart.dto.response.CartResponse;
import com.nhnacademy.frontend.cart.dto.view.CartItemViewModel;
import com.nhnacademy.frontend.cart.service.CartService;
import com.nhnacademy.frontend.common.adapter.BookAdapter;
import feign.FeignException;
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
    public CartViewResponse getCartItems(boolean isLoggedIn, String guestUUID) {
        CartAndOwnerInfo cartAndOwnerInfo = getOrCreateCartAndOwnerInfo(isLoggedIn, guestUUID);
        CartResponse cartResponse = cartAndOwnerInfo.cartResponse();

        List<BookResponse> bookResponseList = bookAdapter.getBooks(cartResponse.getItems().stream()
                .map(CartItemDto::getItemId)
                .toList());

        Map<Long, Integer> itemQuantityMap = cartResponse.getItems().stream()
                .collect(Collectors.toMap(CartItemDto::getItemId, CartItemDto::getQuantity));

        List<CartItemViewModel> cartItemViewModels = bookResponseList.stream()
                .map(item -> new CartItemViewModel(item.id(), item.title(), itemQuantityMap.get(item.id()), item.originalPrice()))
                .collect(Collectors.toList());

        return new CartViewResponse(cartItemViewModels, cartAndOwnerInfo.newGuestUuid());
    }

    @Override
    public CartOperationResult addToCart(Long bookId, int quantity, boolean isLoggedIn, String guestUUID) {
        CartAndOwnerInfo cartAndOwnerInfo = getOrCreateCartAndOwnerInfo(isLoggedIn, guestUUID);
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

        return new CartOperationResult(cartAndOwnerInfo.newGuestUuid());
    }

    @Override
    public CartOperationResult deleteCartItems(List<Long> bookIds, boolean isLoggedIn, String guestUUID) {
        CartAndOwnerInfo cartAndOwnerInfo = getOrCreateCartAndOwnerInfo(isLoggedIn, guestUUID);
        cartAdapter.deleteItemsFromCart(cartAndOwnerInfo.ownerType(), cartAndOwnerInfo.uuid(), bookIds);
        return new CartOperationResult(cartAndOwnerInfo.newGuestUuid());
    }

    @Override
    public CartOperationResult updateCartItems(Map<Long, Integer> quantities, boolean isLoggedIn, String guestUUID) {
        CartAndOwnerInfo cartAndOwnerInfo = getOrCreateCartAndOwnerInfo(isLoggedIn, guestUUID);
        for (Map.Entry<Long, Integer> entry : quantities.entrySet()) {
            Long bookId = entry.getKey();
            Integer quantity = entry.getValue();
            cartAdapter.updateItemQuantity(cartAndOwnerInfo.ownerType(), cartAndOwnerInfo.uuid(), bookId, new CartUpdateRequest(quantity));
        }
        return new CartOperationResult(cartAndOwnerInfo.newGuestUuid());
    }

    private record CartAndOwnerInfo(CartResponse cartResponse, OwnerType ownerType, String uuid, String newGuestUuid) {}

    private CartAndOwnerInfo getOrCreateCartAndOwnerInfo(boolean isLoggedIn, String guestUUID) {
        OwnerType ownerType = isLoggedIn ? OwnerType.USER : OwnerType.GUEST;
        String effectiveUuid = guestUUID;
        CartResponse cartResponse;
        String newGuestUuid = null;

        try {
            cartResponse = cartAdapter.getCart(ownerType, effectiveUuid);
        } catch (FeignException.NotFound e) {
            CartCreateResponse create = cartAdapter.createCart(ownerType);
            if (!isLoggedIn) {
                effectiveUuid = create.getGuestUUID();
                newGuestUuid = effectiveUuid;
            }
            cartResponse = cartAdapter.getCart(ownerType, effectiveUuid);
        }
        return new CartAndOwnerInfo(cartResponse, ownerType, effectiveUuid, newGuestUuid);
    }
}
