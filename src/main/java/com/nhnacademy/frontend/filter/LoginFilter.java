package com.nhnacademy.frontend.filter;

import com.nhnacademy.frontend.domain.LoginResponseDto;
import com.nhnacademy.frontend.service.AuthService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

import java.io.IOException;
import java.util.Collections;

@Slf4j
public class LoginFilter extends AbstractAuthenticationProcessingFilter {
    private final AuthService authService;

    public LoginFilter(String defaultFilterProcessesUrl, AuthService authService, AuthenticationSuccessHandler successHandler, AuthenticationFailureHandler failureHandler) {
        super(defaultFilterProcessesUrl);
        this.authService = authService;
        setAuthenticationSuccessHandler(successHandler);
        setAuthenticationFailureHandler(failureHandler);
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException, IOException, ServletException {
        String username = request.getParameter("username");
        String password = request.getParameter("password");
        log.debug("로그인 시도: username={}, password={}", username, password);

        // 1. AuthService로 인증 요청 및 토큰 수령
        LoginResponseDto loginResponse = authService.login(username, password);

        if(loginResponse != null && loginResponse.getAccessToken() != null) {
            // 2. 인증 객체 생성 (details에 토큰 정보 저장)
            log.info("로그인 성공: username={}", username);
            UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(username, null, Collections.emptyList());
            authToken.setDetails(loginResponse);
            return authToken;
        }
        log.warn("로그인 실패: username={}", username);
        throw new BadCredentialsException("아이디 또는 비밀번호가 올바르지 않습니다.");
    }

    @Override
    protected boolean requiresAuthentication(HttpServletRequest request, HttpServletResponse response) {
        return request.getMethod().equals("POST") && super.requiresAuthentication(request, response);
    }
}
