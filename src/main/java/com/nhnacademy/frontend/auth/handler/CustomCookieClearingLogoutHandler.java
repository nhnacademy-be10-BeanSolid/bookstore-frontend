package com.nhnacademy.frontend.auth.handler;

import com.nhnacademy.frontend.auth.util.JwtCookieUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class CustomCookieClearingLogoutHandler implements LogoutHandler {
    private final JwtCookieUtil jwtCookieUtil;

    @Override
    public void logout(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
        log.info("로그아웃 요청: IP={}, User-Agent={}", request.getRemoteAddr(), request.getHeader("User-Agent"));

        jwtCookieUtil.removeJwtCookie(response);
    }
}
