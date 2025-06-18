package com.nhnacademy.frontend.filter;

import com.nhnacademy.frontend.adapter.AuthAdapter;
import com.nhnacademy.frontend.domain.LoginResponseDto;
import com.nhnacademy.frontend.domain.RefreshTokenRequestDto;
import com.nhnacademy.frontend.provider.JwtTokenProvider;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private final JwtTokenProvider jwtTokenProvider;
    private final AuthAdapter authAdapter;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        String jwt = extractJwtFromCookie(request);

        if(jwt != null && jwtTokenProvider.validateToken(jwt)) {
            // 1. AccessToken이 유효한 경우 기존 인증 처리
            authenticateAndContinue(jwt, request, response, filterChain);
            return;
        }

        // 2. AccessToken이 만료된 경우 RefreshToken 사용
        String refreshToken = extractRefreshTokenFromCookie(request);
        if(refreshToken != null) {
            // AuthAdapter(FeignClient)로 /auth/refresh 요청
            RefreshTokenRequestDto refreshRequest = new RefreshTokenRequestDto(refreshToken);
            try {
                LoginResponseDto loginResponse = authAdapter.refresh(refreshRequest);
                String newAccessToken = loginResponse.getAccessToken();
                String newRefreshToken = loginResponse.getRefreshToken();

                Cookie accessCookie = new Cookie("accessToken", newAccessToken);
                accessCookie.setHttpOnly(true);
                accessCookie.setPath("/");
                accessCookie.setSecure(true);
                accessCookie.setMaxAge(60 * 60);

                Cookie refreshCookie = new Cookie("refreshToken", newRefreshToken);
                refreshCookie.setHttpOnly(true);
                refreshCookie.setPath("/");
                refreshCookie.setSecure(true);
                refreshCookie.setMaxAge(60 * 60 * 24 * 7);

                response.addCookie(accessCookie);
                response.addCookie(refreshCookie);

                authenticateAndContinue(newAccessToken, request, response, filterChain);
            } catch (Exception e) {
                // RefreshToken도 만료/무효이면 인증 실패 처리
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            }
        }
        filterChain.doFilter(request, response);
    }

    private String extractJwtFromCookie(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if(cookies != null) {
            for(Cookie cookie : cookies) {
                if("accessToken".equals(cookie.getName())) {
                    return cookie.getValue();
                }
            }
        }
        return null;
    }

    // 쿠키에서 RefreshToken 추출
    private String extractRefreshTokenFromCookie(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if(cookies != null) {
            for(Cookie cookie : cookies) {
                if("refreshToken".equals(cookie.getName())) {
                    return cookie.getValue();
                }
            }
        }
        return null;
    }

    // 인증 처리 메서드
    private void authenticateAndContinue(String jwt, HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws IOException, ServletException {
        String username = jwtTokenProvider.getUsernameFromJwt(jwt);
        List<GrantedAuthority> authorities = jwtTokenProvider.getAuthorities(jwt);

        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(username, null, authorities);
        authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
        SecurityContextHolder.getContext().setAuthentication(authentication);

        filterChain.doFilter(request, response);
    }
}
