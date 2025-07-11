package com.nhnacademy.frontend.cart.service.impl;

import com.nhnacademy.frontend.cart.adapter.CartAdapter;
import com.nhnacademy.frontend.cart.domain.OwnerType;
import com.nhnacademy.frontend.cart.dto.CartOperationResult;
import com.nhnacademy.frontend.cart.dto.CartViewResponse;
import com.nhnacademy.frontend.cart.dto.request.CartAddItemRequest;
import com.nhnacademy.frontend.cart.dto.request.CartItemUpdateRequest;
import com.nhnacademy.frontend.cart.dto.request.CartUpdateRequest;
import com.nhnacademy.frontend.cart.dto.response.BookResponse;
import com.nhnacademy.frontend.cart.dto.response.CartCreateResponse;
import com.nhnacademy.frontend.cart.dto.response.CartItemDto;
import com.nhnacademy.frontend.cart.dto.response.CartResponse;
import com.nhnacademy.frontend.common.adapter.BookAdapter;
import feign.FeignException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CartServiceImplTest {

    @Mock
    private CartAdapter cartAdapter;

    @Mock
    private BookAdapter bookAdapter;

    @InjectMocks
    private CartServiceImpl cartService;

    private String guestUuid;
    private Long bookId;
    private int quantity;
    private CartItemDto cartItemDto;
    private BookResponse bookResponse;

    @BeforeEach
    void setUp() {
        guestUuid = "test-guest-uuid";
        bookId = 1L;
        quantity = 2;

        cartItemDto = CartItemDto.builder().itemId(bookId).quantity(quantity).build();
        bookResponse = new BookResponse(
                bookId, "Test Book", "description", "toc", "author", "publisher",
                LocalDate.now(), "isbn", 10000, 9000, true, LocalDateTime.now(),
                LocalDateTime.now(), "status", 100
        );
    }

    @Test
    void getCartItems_noItemsInCart() {
        CartResponse emptyCartResponse = CartResponse.builder().cartId(1L).items(Collections.emptyList()).build();
        when(cartAdapter.getCart(any(OwnerType.class), anyString())).thenReturn(emptyCartResponse);

        CartViewResponse response = cartService.getCartItems(false, guestUuid);

        assertTrue(response.cartItems().isEmpty());
        assertNull(response.newGuestUuid());
        verify(cartAdapter, times(1)).getCart(OwnerType.GUEST, guestUuid);
        verify(bookAdapter, never()).getBooks(anyList());
    }

    @Test
    void getCartItems_withItemsInCart_guest() {
        CartResponse cartResponse = CartResponse.builder().cartId(1L).items(Collections.singletonList(cartItemDto)).build();
        when(cartAdapter.getCart(OwnerType.GUEST, guestUuid)).thenReturn(cartResponse);
        when(bookAdapter.getBooks(Collections.singletonList(bookId))).thenReturn(Collections.singletonList(bookResponse));

        CartViewResponse response = cartService.getCartItems(false, guestUuid);

        assertFalse(response.cartItems().isEmpty());
        assertEquals(1, response.cartItems().size());
        assertEquals(bookId, response.cartItems().getFirst().getBookId());
        assertEquals(quantity, response.cartItems().getFirst().getQuantity());
        assertNull(response.newGuestUuid());

        verify(cartAdapter, times(1)).getCart(OwnerType.GUEST, guestUuid);
        verify(bookAdapter, times(1)).getBooks(Collections.singletonList(bookId));
    }

    @Test
    void getCartItems_withItemsInCart_user() {
        CartResponse cartResponse = CartResponse.builder().cartId(1L).items(Collections.singletonList(cartItemDto)).build();
        when(cartAdapter.getCart(OwnerType.USER, null)).thenReturn(cartResponse); // User doesn't have guest UUID
        when(bookAdapter.getBooks(Collections.singletonList(bookId))).thenReturn(Collections.singletonList(bookResponse));

        CartViewResponse response = cartService.getCartItems(true, null);

        assertFalse(response.cartItems().isEmpty());
        assertEquals(1, response.cartItems().size());
        assertEquals(bookId, response.cartItems().getFirst().getBookId());
        assertEquals(quantity, response.cartItems().getFirst().getQuantity());
        assertNull(response.newGuestUuid());

        verify(cartAdapter, times(1)).getCart(OwnerType.USER, null);
        verify(bookAdapter, times(1)).getBooks(Collections.singletonList(bookId));
    }

    @Test
    void getCartItems_cartNotFound_createsNewCart() {
        when(cartAdapter.getCart(any(OwnerType.class), eq(guestUuid)))
                .thenThrow(FeignException.NotFound.class)
                .thenReturn(CartResponse.builder().cartId(1L).items(Collections.emptyList()).build());

        CartCreateResponse createResponse = CartCreateResponse.builder()
                .guestUUID(guestUuid)
                .cartId(1L)
                .ownerType(OwnerType.GUEST)
                .createdAt(LocalDateTime.now())
                .userId(null)
                .build();
        when(cartAdapter.createCart(any(OwnerType.class)))
                .thenReturn(createResponse);

        CartViewResponse response = cartService.getCartItems(false, guestUuid);

        assertTrue(response.cartItems().isEmpty());
        assertEquals(guestUuid, response.newGuestUuid());
        verify(cartAdapter, times(2)).getCart(OwnerType.GUEST, guestUuid);
        verify(cartAdapter, times(1)).createCart(OwnerType.GUEST);
    }

    @Test
    void addToCart_addNewItem() {
        CartResponse emptyCartResponse = CartResponse.builder().cartId(1L).items(Collections.emptyList()).build();
        when(cartAdapter.getCart(any(OwnerType.class), anyString())).thenReturn(emptyCartResponse);
        when(cartAdapter.addItemToCart(any(OwnerType.class), anyString(), any(CartAddItemRequest.class)))
                .thenReturn(CartResponse.builder().cartId(1L).items(Collections.emptyList()).build());

        CartOperationResult result = cartService.addToCart(bookId, quantity, false, guestUuid);

        assertNull(result.newGuestUuid());
        verify(cartAdapter, times(1)).getCart(OwnerType.GUEST, guestUuid);
        verify(cartAdapter, times(1)).addItemToCart(OwnerType.GUEST, guestUuid, new CartAddItemRequest(bookId, quantity));
        verify(cartAdapter, never()).updateItemQuantity(any(OwnerType.class), anyString(), anyLong(), any(CartUpdateRequest.class));
    }

    @Test
    void addToCart_updateExistingItemQuantity() {
        CartResponse existingCartResponse = CartResponse.builder().cartId(1L).items(Collections.singletonList(cartItemDto)).build();
        when(cartAdapter.getCart(any(OwnerType.class), anyString())).thenReturn(existingCartResponse);
        when(cartAdapter.updateItemQuantity(any(OwnerType.class), anyString(), anyLong(), any(CartUpdateRequest.class)))
                .thenReturn(CartResponse.builder().cartId(1L).items(Collections.emptyList()).build());

        CartOperationResult result = cartService.addToCart(bookId, 5, false, guestUuid);

        assertNull(result.newGuestUuid());
        verify(cartAdapter, times(1)).getCart(OwnerType.GUEST, guestUuid);
        verify(cartAdapter, times(1)).updateItemQuantity(OwnerType.GUEST, guestUuid, bookId, new CartUpdateRequest(5));
        verify(cartAdapter, never()).addItemToCart(any(OwnerType.class), anyString(), any(CartAddItemRequest.class));
    }

    @Test
    void deleteCartItems() {
        List<Long> bookIdsToDelete = Collections.singletonList(bookId);
        CartResponse cartResponse = CartResponse.builder().cartId(1L).items(Collections.singletonList(cartItemDto)).build();
        when(cartAdapter.getCart(any(OwnerType.class), anyString())).thenReturn(cartResponse);
        when(cartAdapter.deleteItemsFromCart(any(OwnerType.class), anyString(), anyList()))
                .thenReturn(CartResponse.builder().cartId(1L).items(Collections.emptyList()).build());

        CartOperationResult result = cartService.deleteCartItems(bookIdsToDelete, false, guestUuid);

        assertNull(result.newGuestUuid());
        verify(cartAdapter, times(1)).getCart(OwnerType.GUEST, guestUuid);
        verify(cartAdapter, times(1)).deleteItemsFromCart(OwnerType.GUEST, guestUuid, bookIdsToDelete);
    }

    @Test
    void updateCartItems() {
        List<CartItemUpdateRequest> updates = Arrays.asList(
                new CartItemUpdateRequest(bookId, 5),
                new CartItemUpdateRequest(2L, 3)
        );
        CartResponse cartResponse = CartResponse.builder().cartId(1L).items(Collections.singletonList(cartItemDto)).build();
        when(cartAdapter.getCart(any(OwnerType.class), anyString())).thenReturn(cartResponse);
        when(cartAdapter.updateItemQuantity(any(OwnerType.class), anyString(), anyLong(), any(CartUpdateRequest.class)))
                .thenReturn(CartResponse.builder().cartId(1L).items(Collections.emptyList()).build());

        CartOperationResult result = cartService.updateCartItems(updates, false, guestUuid);

        assertNull(result.newGuestUuid());
        verify(cartAdapter, times(1)).getCart(OwnerType.GUEST, guestUuid);
        verify(cartAdapter, times(1)).updateItemQuantity(OwnerType.GUEST, guestUuid, bookId, new CartUpdateRequest(5));
        verify(cartAdapter, times(1)).updateItemQuantity(OwnerType.GUEST, guestUuid, 2L, new CartUpdateRequest(3));
    }
}