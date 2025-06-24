package com.nhnacademy.frontend.auth.filter;

import com.nhnacademy.frontend.auth.domain.RefreshTokenResponseDto;
import com.nhnacademy.frontend.auth.domain.TokenParseResponseDto;
import com.nhnacademy.frontend.auth.service.AuthService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private final AuthService authService;

    @Value("${custom.security.jwt.access-token-expiration}")
    private int accessTokenExpiration;

    @Value("${custom.security.jwt.refresh-token-expiration}")
    private int refreshTokenExpiration;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        String jwt = extractJwtFromCookie(request);

        if(jwt != null && authService.validate(jwt)) {
            // 1. AccessToken이 유효한 경우 기존 인증 처리
            authenticateAndContinue(jwt, request, response, filterChain);
            return;
        }

        // 2. AccessToken이 만료된 경우 RefreshToken 사용
        String refreshToken = extractRefreshTokenFromCookie(request);
        if(refreshToken != null) {
            // AuthAdapter(FeignClient)로 /auth/refresh 요청
            try {
                RefreshTokenResponseDto refresh = authService.refresh(refreshToken);
                String newAccessToken = refresh.getAccessToken();
                String newRefreshToken = refresh.getRefreshToken();

                Cookie accessCookie = new Cookie("accessToken", newAccessToken);
                accessCookie.setHttpOnly(true);
                accessCookie.setPath("/");
                accessCookie.setSecure(true);
                accessCookie.setMaxAge(accessTokenExpiration);

                Cookie refreshCookie = new Cookie("refreshToken", newRefreshToken);
                refreshCookie.setHttpOnly(true);
                refreshCookie.setPath("/");
                refreshCookie.setSecure(true);
                refreshCookie.setMaxAge(refreshTokenExpiration);

                response.addCookie(accessCookie);
                response.addCookie(refreshCookie);

                authenticateAndContinue(newAccessToken, request, response, filterChain);
                return;
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
        try {
            TokenParseResponseDto parsed = authService.parse(jwt);
            String username = parsed.username();
            List<String> authorities = parsed.authorities();

            List<GrantedAuthority> grantedAuthorities = AuthorityUtils.createAuthorityList(authorities.toArray(new String[0]));

            UsernamePasswordAuthenticationToken authentication =
                    new UsernamePasswordAuthenticationToken(username, null, grantedAuthorities);
            authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
            SecurityContextHolder.getContext().setAuthentication(authentication);
        } catch (Exception e) {
            SecurityContextHolder.clearContext();
        }
        filterChain.doFilter(request, response);
    }
}
