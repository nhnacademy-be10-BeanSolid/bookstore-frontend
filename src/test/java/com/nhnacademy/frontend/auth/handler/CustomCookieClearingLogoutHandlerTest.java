package com.nhnacademy.frontend.auth.handler;

import com.nhnacademy.frontend.auth.domain.LoginResponseDto;
import com.nhnacademy.frontend.auth.util.JwtCookieUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CustomCookieClearingLogoutHandlerTest {
    @Mock
    JwtCookieUtil jwtCookieUtil;

    @Mock
    HttpServletRequest request;

    @Mock
    HttpServletResponse response;

    @Mock
    Authentication authentication;

    @InjectMocks
    LoginSuccessHandler loginSuccessHandler;

    @Test
    void logout_shouldClearAccessTokenAndRefreshTokenCookies() throws Exception {
        // given
        LoginResponseDto loginResponse = new LoginResponseDto("accessToken", "refreshToken");
        when(authentication.getDetails()).thenReturn(loginResponse);

        // when
        loginSuccessHandler.onAuthenticationSuccess(request, response, authentication);

        // then
        verify(jwtCookieUtil).addJwtCookie(response, "accessToken", "refreshToken");
        verify(response).sendRedirect("/");
    }
}