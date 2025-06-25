package com.nhnacademy.frontend.auth.filter;

import com.nhnacademy.frontend.auth.domain.LoginResponseDto;
import com.nhnacademy.frontend.auth.domain.TokenParseResponseDto;
import com.nhnacademy.frontend.auth.service.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class LoginFilterTest {

    @Mock
    AuthService authService;

    @Mock
    AuthenticationSuccessHandler successHandler;

    @Mock
    AuthenticationFailureHandler failureHandler;

    @Mock
    HttpServletRequest request;

    @Mock
    HttpServletResponse response;

    LoginFilter loginFilter;

    @BeforeEach
    void setUp() {
        loginFilter = new LoginFilter("/login", authService, successHandler, failureHandler);
    }

    @Test
    void attemptAuthentication_success_returnsAuthentication() throws Exception {
        String username = "user1";
        String password = "pw123";
        String accessToken = "access-token";
        String refreshToken = "refresh-token";
        List<String> authorities = List.of("ROLE_USER");

        when(request.getParameter("username")).thenReturn(username);
        when(request.getParameter("password")).thenReturn(password);
        when(authService.login(username, password))
                .thenReturn(new LoginResponseDto(accessToken, refreshToken));
        when(authService.parse(accessToken))
                .thenReturn(new TokenParseResponseDto(username, authorities));

        Authentication authentication = loginFilter.attemptAuthentication(request, response);

        assertThat(authentication).isInstanceOf(UsernamePasswordAuthenticationToken.class);
        assertThat(authentication.getPrincipal()).isEqualTo(username);
        assertThat(authentication.isAuthenticated()).isTrue();
        assertThat(authentication.getAuthorities()).extracting("authority").containsExactlyElementsOf(authorities);

        assertThat(authentication.getDetails()).isInstanceOf(LoginResponseDto.class);
        assertThat(((LoginResponseDto) authentication.getDetails()).getAccessToken()).isEqualTo(accessToken);
    }

    @Test
    void attemptAuthentication_invalidCredentials_throwsException() {
        String username = "user1";
        String password = "wrong";
        when(request.getParameter("username")).thenReturn(username);
        when(request.getParameter("password")).thenReturn(password);
        when(authService.login(username, password)).thenReturn(null);

        assertThatThrownBy(() -> loginFilter.attemptAuthentication(request, response))
                .isInstanceOf(BadCredentialsException.class)
                .hasMessageContaining("아이디 또는 비밀번호가 올바르지 않습니다.");
    }
}