package com.nhnacademy.frontend.auth.handler;

import com.nhnacademy.frontend.auth.domain.LoginResponseDto;
import com.nhnacademy.frontend.auth.util.JwtCookieUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class LoginSuccessHandler implements AuthenticationSuccessHandler {
    private final JwtCookieUtil jwtCookieUtil;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        LoginResponseDto loginResponse = (LoginResponseDto) authentication.getDetails();
        String accessToken = loginResponse.getAccessToken();
        String refreshToken = loginResponse.getRefreshToken();

        jwtCookieUtil.addJwtCookie(response, accessToken, refreshToken);

        response.sendRedirect("/");
    }
}
