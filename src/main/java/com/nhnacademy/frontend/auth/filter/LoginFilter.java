package com.nhnacademy.frontend.auth.filter;

import com.nhnacademy.frontend.auth.dto.response.LoginResponseDto;
import com.nhnacademy.frontend.auth.dto.response.TokenParseResponseDto;
import com.nhnacademy.frontend.auth.service.AuthService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

import java.io.IOException;
import java.util.List;

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
        log.debug("로그인 시도: username={}", username);

        try {
            // 인증 요청 및 토큰 수령
            LoginResponseDto loginResponse = authService.login(username, password);

            if (loginResponse != null && loginResponse.getAccessToken() != null) {
                // 정상 로그인 처리
                TokenParseResponseDto parsed = authService.parse(loginResponse.getAccessToken());
                List<String> authorities = parsed.authorities();
                var grantedAuthorities = AuthorityUtils.createAuthorityList(authorities.toArray(new String[0]));
                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                        username, null, grantedAuthorities);
                authToken.setDetails(loginResponse);
                return authToken;
            } else {
                throw new BadCredentialsException("아이디 또는 비밀번호가 올바르지 않습니다.");
            }
        } catch (Exception ex) {
            // 인증 실패 시 응답에서 메시지 추출
            String errorMessage = ex.getMessage();
            log.error(errorMessage);

            // "휴면" 또는 "휴먼"이 포함된 경우 dormant.html로 리다이렉트
            if (errorMessage != null && (errorMessage.contains("휴면"))) {
                response.sendRedirect("/auth/login/dormant?userId=" + username);
                return null;
            }

            // 그 외 예외는 기존 방식으로 처리
            throw new BadCredentialsException(errorMessage != null ? errorMessage : "로그인 실패");
        }
    }

    @Override
    protected boolean requiresAuthentication(HttpServletRequest request, HttpServletResponse response) {
        return request.getMethod().equals("POST") && super.requiresAuthentication(request, response);
    }
}
