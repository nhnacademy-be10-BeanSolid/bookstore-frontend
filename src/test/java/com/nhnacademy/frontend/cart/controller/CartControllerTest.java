package com.nhnacademy.frontend.cart.controller;

import com.nhnacademy.frontend.auth.filter.JwtAuthenticationFilter;
import com.nhnacademy.frontend.cart.dto.CartOperationResult;
import com.nhnacademy.frontend.cart.dto.CartViewResponse;
import com.nhnacademy.frontend.cart.dto.request.CartItemUpdateRequest;
import com.nhnacademy.frontend.cart.dto.request.CartUpdateQuantitiesRequest;
import com.nhnacademy.frontend.cart.dto.view.CartItemViewModel;
import com.nhnacademy.frontend.cart.service.CartService;
import jakarta.servlet.http.Cookie;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(
        controllers = CartController.class,
        excludeFilters = @ComponentScan.Filter(
                type = FilterType.ASSIGNABLE_TYPE,
                classes = {JwtAuthenticationFilter.class}
        )
)
@AutoConfigureMockMvc(addFilters = false)
class CartControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CartService cartService;

    @MockBean
    private RedisConnectionFactory redisConnectionFactory;

    private String guestUuid;
    private Long bookId;
    private int quantity;
    private CartOperationResult cartOperationResult;
    private CartViewResponse cartViewResponse;

    @BeforeEach
    void setUp() {
        guestUuid = "test-guest-uuid";
        bookId = 1L;
        quantity = 2;

        CartItemViewModel cartItemViewModel = new CartItemViewModel(bookId, "Test Book", quantity, 10000L);
        cartOperationResult = new CartOperationResult(null);
        cartViewResponse = new CartViewResponse(Collections.singletonList(cartItemViewModel), null);
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void cartForm_loggedInUser_noGuestUuid() throws Exception {
        Authentication authentication = new UsernamePasswordAuthenticationToken("testUser", "password", Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER")));
        SecurityContextHolder.getContext().setAuthentication(authentication);

        when(cartService.getCartItems(eq(true), eq(null))).thenReturn(cartViewResponse);

        mockMvc.perform(get("/cart").sessionAttr("isLoggedIn", true))
                .andExpect(status().isOk())
                .andExpect(view().name("cart/cartForm"))
                .andExpect(model().attributeExists("cartItems"))
                .andExpect(model().attributeExists("cartUpdateQuantitiesRequest"))
                .andExpect(cookie().doesNotExist("guest_uuid"));

        verify(cartService, times(1)).getCartItems(eq(true), eq(null));
    }

    @Test
    void cartForm_guestUser_noGuestUuid_createsNew() throws Exception {
        CartViewResponse newGuestCartResponse = new CartViewResponse(Collections.emptyList(), guestUuid);
        when(cartService.getCartItems(false, null)).thenReturn(newGuestCartResponse);

        mockMvc.perform(get("/cart").sessionAttr("isLoggedIn", false))
                .andExpect(status().isOk())
                .andExpect(view().name("cart/cartForm"))
                .andExpect(model().attributeExists("cartItems"))
                .andExpect(model().attributeExists("cartUpdateQuantitiesRequest"))
                .andExpect(cookie().value("guest_uuid", guestUuid))
                .andExpect(cookie().httpOnly("guest_uuid", true))
                .andExpect(cookie().path("guest_uuid", "/"))
                .andExpect(cookie().maxAge("guest_uuid", 60 * 60 * 24 * 30));

        verify(cartService, times(1)).getCartItems(false, null);
    }

    @Test
    void cartForm_guestUser_withGuestUuid() throws Exception {
        when(cartService.getCartItems(eq(false), eq(guestUuid))).thenReturn(cartViewResponse);

        mockMvc.perform(get("/cart").sessionAttr("isLoggedIn", false).cookie(new Cookie("guest_uuid", guestUuid)))
                .andExpect(status().isOk())
                .andExpect(view().name("cart/cartForm"))
                .andExpect(model().attributeExists("cartItems"))
                .andExpect(model().attributeExists("cartUpdateQuantitiesRequest"));

        verify(cartService, times(1)).getCartItems(false, guestUuid);
    }

    @Test
    void addToCart_addNewItem() throws Exception {
        when(cartService.addToCart(anyLong(), anyInt(), eq(false), eq(guestUuid))).thenReturn(cartOperationResult);

        mockMvc.perform(post("/cart/add")
                        .param("bookId", String.valueOf(bookId))
                        .param("quantity", String.valueOf(quantity))
                        .sessionAttr("isLoggedIn", false)
                        .cookie(new Cookie("guest_uuid", guestUuid)))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/cart"));

        verify(cartService, times(1)).addToCart(bookId, quantity, false, guestUuid);
    }

    @Test
    void addToCart_addNewItem_createsNewGuestUuid() throws Exception {
        CartOperationResult newGuestUuidResult = new CartOperationResult(guestUuid);
        when(cartService.addToCart(anyLong(), anyInt(), eq(false), eq(null))).thenReturn(newGuestUuidResult);

        mockMvc.perform(post("/cart/add")
                        .param("bookId", String.valueOf(bookId))
                        .param("quantity", String.valueOf(quantity))
                        .sessionAttr("isLoggedIn", false))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/cart"))
                .andExpect(cookie().value("guest_uuid", guestUuid));

        verify(cartService, times(1)).addToCart(bookId, quantity, false, null);
    }

    @Test
    void deleteFromCart() throws Exception {
        when(cartService.deleteCartItems(anyList(), eq(false), eq(guestUuid))).thenReturn(cartOperationResult);

        mockMvc.perform(post("/cart/delete")
                        .param("bookId", String.valueOf(bookId))
                        .sessionAttr("isLoggedIn", false)
                        .cookie(new Cookie("guest_uuid", guestUuid)))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/cart"));

        verify(cartService, times(1)).deleteCartItems(Collections.singletonList(bookId), false, guestUuid);
    }

    @Test
    void updateCart() throws Exception {
        CartItemUpdateRequest updateRequest = new CartItemUpdateRequest(bookId, 5);
        CartUpdateQuantitiesRequest request = new CartUpdateQuantitiesRequest();
        request.setUpdates(Collections.singletonList(updateRequest));

        when(cartService.updateCartItems(anyList(), eq(false), eq(guestUuid))).thenReturn(cartOperationResult);

        mockMvc.perform(post("/cart/update")
                        .flashAttr("cartUpdateQuantitiesRequest", request)
                        .sessionAttr("isLoggedIn", false)
                        .cookie(new Cookie("guest_uuid", guestUuid)))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/cart"));

        verify(cartService, times(1)).updateCartItems(Collections.singletonList(updateRequest), false, guestUuid);
    }
}