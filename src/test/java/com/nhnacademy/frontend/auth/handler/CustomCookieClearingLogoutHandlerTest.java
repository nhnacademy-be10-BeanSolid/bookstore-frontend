package com.nhnacademy.frontend.auth.handler;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class CustomCookieClearingLogoutHandlerTest {
    @Mock
    HttpServletRequest request;

    @Mock
    HttpServletResponse response;

    @Mock
    Authentication authentication;

    @Test
    void logout_shouldClearAccessTokenAndRefreshTokenCookies() {
        CustomCookieClearingLogoutHandler handler = new CustomCookieClearingLogoutHandler();

        handler.logout(request, response, authentication);

        ArgumentCaptor<Cookie> cookieCaptor = ArgumentCaptor.forClass(Cookie.class);
        verify(response, times(2)).addCookie(cookieCaptor.capture());

        assertThat(cookieCaptor.getAllValues()).hasSize(2);

        Cookie cookie1 = cookieCaptor.getAllValues().get(0);
        Cookie cookie2 = cookieCaptor.getAllValues().get(1);

        assertThat(List.of(cookie1.getName(), cookie2.getName()))
                .containsExactlyInAnyOrder("accessToken", "refreshToken");

        for(Cookie cookie : List.of(cookie1, cookie2)) {
            assertThat(cookie.getValue()).isNull();
            assertThat(cookie.getPath()).isEqualTo("/");
            assertThat(cookie.getMaxAge()).isZero();
            assertThat(cookie.isHttpOnly()).isTrue();
            assertThat(cookie.getSecure()).isTrue();
        }
    }
}