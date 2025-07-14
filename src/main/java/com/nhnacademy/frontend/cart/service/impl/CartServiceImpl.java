package com.nhnacademy.frontend.cart.service.impl;

import com.nhnacademy.frontend.cart.adapter.CartAdapter;
import com.nhnacademy.frontend.cart.domain.OwnerType;
import com.nhnacademy.frontend.cart.dto.CartOperationResult;
import com.nhnacademy.frontend.cart.dto.CartViewResponse;
import com.nhnacademy.frontend.cart.dto.request.CartAddItemRequest;
import com.nhnacademy.frontend.cart.dto.request.CartItemUpdateRequest;
import com.nhnacademy.frontend.cart.dto.request.CartUpdateItemsRequest;
import com.nhnacademy.frontend.cart.dto.request.CartUpdateRequest;
import com.nhnacademy.frontend.common.adapter.dto.book.response.BookResponse;
import com.nhnacademy.frontend.cart.dto.response.CartCreateResponse;
import com.nhnacademy.frontend.cart.dto.response.CartItemDto;
import com.nhnacademy.frontend.cart.dto.response.CartResponse;
import com.nhnacademy.frontend.cart.dto.view.CartItemViewModel;
import com.nhnacademy.frontend.cart.service.CartService;
import com.nhnacademy.frontend.common.adapter.BookAdapter;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
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

        List<Long> bookIds = cartResponse.getItems().stream().map(CartItemDto::getItemId).toList();
        if (bookIds.isEmpty()) {
            return new CartViewResponse(Collections.emptyList(), cartAndOwnerInfo.newGuestUuid());
        }

        Map<Long, BookResponse> bookDetailsMap = bookAdapter.getBooks(bookIds).stream()
                .collect(Collectors.toMap(BookResponse::id, Function.identity()));

        List<CartItemViewModel> viewModels = cartResponse.getItems().stream()
                .map(cartItem -> {
                    BookResponse book = bookDetailsMap.get(cartItem.getItemId());
                    return new CartItemViewModel(book.id(), book.title(), cartItem.getQuantity(), book.originalPrice());
                })
                .collect(Collectors.toList());

        return new CartViewResponse(viewModels, cartAndOwnerInfo.newGuestUuid());
    }

    @Override
    public CartOperationResult addToCart(Long bookId, int quantity, boolean isLoggedIn, String guestUUID) {
        CartAndOwnerInfo cartAndOwnerInfo = getOrCreateCartAndOwnerInfo(isLoggedIn, guestUUID);

        cartAndOwnerInfo.cartResponse().getItems().stream()
                .filter(item -> item.getItemId() == bookId)
                .findFirst()
                .ifPresentOrElse(
                        item -> cartAdapter.updateItemQuantity(cartAndOwnerInfo.ownerType(), cartAndOwnerInfo.uuid(), bookId, new CartUpdateRequest(quantity)),
                        () -> cartAdapter.addItemToCart(cartAndOwnerInfo.ownerType(), cartAndOwnerInfo.uuid(), new CartAddItemRequest(bookId, quantity))
                );

        return new CartOperationResult(cartAndOwnerInfo.newGuestUuid());
    }

    @Override
    public CartOperationResult deleteCartItems(List<Long> bookIds, boolean isLoggedIn, String guestUUID) {
        CartAndOwnerInfo cartAndOwnerInfo = getOrCreateCartAndOwnerInfo(isLoggedIn, guestUUID);
        cartAdapter.deleteItemsFromCart(cartAndOwnerInfo.ownerType(), cartAndOwnerInfo.uuid(), bookIds);
        return new CartOperationResult(cartAndOwnerInfo.newGuestUuid());
    }

    @Override
    public CartOperationResult updateCartItems(List<CartItemUpdateRequest> updates, boolean isLoggedIn, String guestUUID) {
        CartAndOwnerInfo cartAndOwnerInfo = getOrCreateCartAndOwnerInfo(isLoggedIn, guestUUID);
        List<CartUpdateItemsRequest.CartItemUpdate> items = updates.stream()
                .map(update -> new CartUpdateItemsRequest.CartItemUpdate(update.getBookId(), update.getQuantity()))
                .toList();
        cartAdapter.updateItemsInCart(cartAndOwnerInfo.ownerType(), cartAndOwnerInfo.uuid(), new CartUpdateItemsRequest(items));
        return new CartOperationResult(cartAndOwnerInfo.newGuestUuid());
    }

    private record CartAndOwnerInfo(CartResponse cartResponse, OwnerType ownerType, String uuid, String newGuestUuid) {}

    private CartAndOwnerInfo getOrCreateCartAndOwnerInfo(boolean isLoggedIn, String guestUUID) {
        OwnerType ownerType = isLoggedIn ? OwnerType.USER : OwnerType.GUEST;
        try {
            CartResponse cartResponse = cartAdapter.getCart(ownerType, guestUUID);
            return new CartAndOwnerInfo(cartResponse, ownerType, guestUUID, null);
        } catch (FeignException.NotFound e) {
            return createNewCartAndGetInfo(ownerType);
        }
    }

    private CartAndOwnerInfo createNewCartAndGetInfo(OwnerType ownerType) {
        CartCreateResponse createResponse = cartAdapter.createCart(ownerType);
        String newGuestUuid = null;
        String effectiveUuid = null;

        if (ownerType == OwnerType.GUEST) {
            newGuestUuid = createResponse.getGuestUUID();
            effectiveUuid = newGuestUuid;
        }

        CartResponse cartResponse = cartAdapter.getCart(ownerType, effectiveUuid);
        return new CartAndOwnerInfo(cartResponse, ownerType, effectiveUuid, newGuestUuid);
    }
}
