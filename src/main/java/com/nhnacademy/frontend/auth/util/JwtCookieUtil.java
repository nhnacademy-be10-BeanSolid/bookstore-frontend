package com.nhnacademy.frontend.auth.util;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class JwtCookieUtil {
    @Value("${custom.security.jwt.access-token-expiration}")
    private int accessTokenExpiration;

    @Value("${custom.security.jwt.refresh-token-expiration}")
    private int refreshTokenExpiration;

    public void addJwtCookie(HttpServletResponse response, String accessToken, String refreshToken) {
        Cookie jwtCookie = new Cookie("accessToken", accessToken);
        jwtCookie.setHttpOnly(true);
        jwtCookie.setSecure(true);
        jwtCookie.setPath("/");
        jwtCookie.setMaxAge(accessTokenExpiration);

        Cookie refreshCookie = new Cookie("refreshToken", refreshToken);
        refreshCookie.setHttpOnly(true);
        refreshCookie.setSecure(true);
        refreshCookie.setPath("/");
        refreshCookie.setMaxAge(refreshTokenExpiration);

        response.addCookie(jwtCookie);
        response.addCookie(refreshCookie);
    }

    public void removeJwtCookie(HttpServletResponse response) {
        Cookie accessToken = new Cookie("accessToken", null);
        accessToken.setPath("/");
        accessToken.setMaxAge(0);
        accessToken.setHttpOnly(true);
        accessToken.setSecure(true);
        response.addCookie(accessToken);

        Cookie refreshToken = new Cookie("refreshToken", null);
        refreshToken.setPath("/");
        refreshToken.setMaxAge(0);
        refreshToken.setHttpOnly(true);
        refreshToken.setSecure(true);
        response.addCookie(refreshToken);
    }
}
