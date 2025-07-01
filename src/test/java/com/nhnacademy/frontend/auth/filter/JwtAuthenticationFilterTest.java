package com.nhnacademy.frontend.auth.filter;

import com.nhnacademy.frontend.auth.domain.response.RefreshTokenResponseDto;
import com.nhnacademy.frontend.auth.domain.response.TokenParseResponseDto;
import com.nhnacademy.frontend.auth.service.AuthService;
import com.nhnacademy.frontend.auth.util.JwtCookieUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class JwtAuthenticationFilterTest {
    @Mock
    AuthService authService;

    @Mock
    HttpServletRequest request;

    @Mock
    HttpServletResponse response;

    @Mock
    FilterChain filterChain;

    @Mock
    JwtCookieUtil jwtCookieUtil;

    @InjectMocks
    JwtAuthenticationFilter filter;

    @BeforeEach
    void setUp() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void validAccessToken_authenticatesAndContinues() throws Exception {
        Cookie[] cookies = { new Cookie("accessToken", "valid-access") };
        when(request.getCookies()).thenReturn(cookies);
        when(authService.validate("valid-access")).thenReturn(true);
        when(authService.parse("valid-access")).thenReturn(new TokenParseResponseDto("user", List.of("ROLE_USER")));

        filter.doFilterInternal(request, response, filterChain);

        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNotNull();
        verify(filterChain).doFilter(request, response);
        verify(authService, never()).refresh(any());
    }

    @Test
    void expiredAccessToken_validRefreshToken_refreshesAndAuthenticates() throws Exception {
        Cookie[] cookies = {
                new Cookie("accessToken", "expired-access"),
                new Cookie("refreshToken", "valid-refresh")
        };
        when(request.getCookies()).thenReturn(cookies);
        when(authService.validate("expired-access")).thenReturn(false);
        when(authService.refresh("valid-refresh")).thenReturn(new RefreshTokenResponseDto("new-access", "new-refresh"));
        when(authService.parse("new-access")).thenReturn(new TokenParseResponseDto("user", List.of("ROLE_ADMIN")));

        filter.doFilterInternal(request, response, filterChain);

        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNotNull();
        verify(jwtCookieUtil).addJwtCookie(response, "new-access", "new-refresh");
        verify(filterChain).doFilter(request, response);
    }

    @Test
    void expiredAccessToken_expiredRefreshToken_setsUnauthorized() throws Exception {
        Cookie[] cookies = {
                new Cookie("accessToken", "expired-access"),
                new Cookie("refreshToken", "expired-refresh")
        };
        when(request.getCookies()).thenReturn(cookies);
        when(authService.validate("expired-access")).thenReturn(false);
        when(authService.refresh("expired-refresh")).thenThrow(new RuntimeException("expired"));

        filter.doFilterInternal(request, response, filterChain);

        verify(response).setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        verify(filterChain).doFilter(request, response);

        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
    }

    @Test
    void noCookies_doesNotAuthenticate() throws Exception {
        when(request.getCookies()).thenReturn(null);

        filter.doFilterInternal(request, response, filterChain);

        verify(filterChain).doFilter(request, response);

        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
    }

    @Test
    void noAccessTokenCookie_doesNotAuthenticate() throws Exception {
        Cookie[] cookies = { new Cookie("other", "value")};
        when(request.getCookies()).thenReturn(cookies);

        filter.doFilterInternal(request, response, filterChain);

        verify(filterChain).doFilter(request, response);

        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
    }

    @Test
    void validAccessToken_parseThrowsException_setsNoAuthentication() throws Exception {
        Cookie[] cookies = { new Cookie("accessToken", "valid-access") };
        when(request.getCookies()).thenReturn(cookies);
        when(authService.validate("valid-access")).thenReturn(true);
        when(authService.parse("valid-access")).thenThrow(new RuntimeException("parse error"));

        filter.doFilterInternal(request, response, filterChain);

        verify(filterChain).doFilter(request, response);

        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
    }
}