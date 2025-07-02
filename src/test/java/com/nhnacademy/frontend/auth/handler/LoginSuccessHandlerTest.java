package com.nhnacademy.frontend.auth.handler;

import com.nhnacademy.frontend.auth.domain.response.LoginResponseDto;
import com.nhnacademy.frontend.auth.util.JwtCookieUtil;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class LoginSuccessHandlerTest {
    @Mock
    HttpServletRequest request;

    @Mock
    HttpServletResponse response;

    @Mock
    Authentication authentication;

    @Mock
    JwtCookieUtil jwtCookieUtil;

    @InjectMocks
    LoginSuccessHandler handler;

    @Test
    void onAuthenticationSuccess_setsCookiesAndRedirect() throws Exception {
        String accessToken = "accessToken";
        String refreshToken = "refreshToken";
        LoginResponseDto loginResponseDto = new LoginResponseDto(accessToken, refreshToken);
        when(authentication.getDetails()).thenReturn(loginResponseDto);

        ArgumentCaptor<Cookie> cookieCaptor = ArgumentCaptor.forClass(Cookie.class);

        handler.onAuthenticationSuccess(request, response, authentication);

        // JwtCookieUtil.addJwtCookie가 호출되는지 검증
        verify(jwtCookieUtil).addJwtCookie(response, accessToken, refreshToken);
        // 리다이렉트도 검증
        verify(response).sendRedirect("/");
    }
}