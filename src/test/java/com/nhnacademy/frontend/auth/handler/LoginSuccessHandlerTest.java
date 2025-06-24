package com.nhnacademy.frontend.auth.handler;

import com.nhnacademy.frontend.auth.domain.LoginResponseDto;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;
import static org.springframework.test.util.ReflectionTestUtils.setField;

@ExtendWith(MockitoExtension.class)
class LoginSuccessHandlerTest {
    @Mock
    HttpServletRequest request;

    @Mock
    HttpServletResponse response;

    @Mock
    Authentication authentication;

    LoginSuccessHandler handler;

    @BeforeEach
    void setUp() {
        handler = new LoginSuccessHandler();
        setField(handler, "accessTokenExpiration", 3600);
        setField(handler, "refreshTokenExpiration", 7200);
    }

    @Test
    void onAuthenticationSuccess_setsCookiesAndRedirect() throws Exception {
        String accessToken = "accessToken";
        String refreshToken = "refreshToken";
        LoginResponseDto loginResponseDto = new LoginResponseDto(accessToken, refreshToken);

        when(authentication.getDetails()).thenReturn(loginResponseDto);

        ArgumentCaptor<Cookie> cookieCaptor = ArgumentCaptor.forClass(Cookie.class);

        handler.onAuthenticationSuccess(request, response, authentication);

        verify(response, times(2)).addCookie(cookieCaptor.capture());
        List<Cookie> cookies = cookieCaptor.getAllValues();

        assertThat(cookies).hasSize(2);

        Cookie accessCookie = cookies.stream()
                .filter(c -> "accessToken".equals(c.getName()))
                .findFirst().orElseThrow();
        Cookie refreshCookie = cookies.stream()
                .filter(c -> "refreshToken".equals(c.getName()))
                .findFirst().orElseThrow();

        assertThat(accessCookie.getValue()).isEqualTo(accessToken);
        assertThat(accessCookie.isHttpOnly()).isTrue();
        assertThat(accessCookie.getSecure()).isTrue();
        assertThat(accessCookie.getPath()).isEqualTo("/");
        assertThat(accessCookie.getMaxAge()).isEqualTo(3600);

        assertThat(refreshCookie.getValue()).isEqualTo(refreshToken);
        assertThat(refreshCookie.isHttpOnly()).isTrue();
        assertThat(refreshCookie.getSecure()).isTrue();
        assertThat(refreshCookie.getPath()).isEqualTo("/");
        assertThat(refreshCookie.getMaxAge()).isEqualTo(7200);

        verify(response).sendRedirect("/");
    }
}