package com.nhnacademy.frontend.auth.service;

import com.nhnacademy.frontend.adapter.AuthAdapter;
import com.nhnacademy.frontend.auth.domain.LoginRequestDto;
import com.nhnacademy.frontend.auth.domain.LoginResponseDto;
import com.nhnacademy.frontend.auth.domain.RefreshTokenResponseDto;
import com.nhnacademy.frontend.auth.domain.TokenParseResponseDto;
import feign.FeignException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
class AuthServiceImplTest {

    @Mock
    AuthAdapter authAdapter;

    @InjectMocks
    AuthServiceImpl authService;

    @Test
    void login_success_returnsLoginResponseDto() {
        String username = "user1";
        String password = "pw123";
        LoginRequestDto requestDto = new LoginRequestDto(username, password);
        LoginResponseDto responseDto = new LoginResponseDto("access-token", "refresh-token");

        when(authAdapter.login(requestDto)).thenReturn(responseDto);

        LoginResponseDto result = authService.login(username, password);

        assertThat(result).isEqualTo(responseDto);
    }

    @Test
    void login_feignException_returnsNull() {
        String username = "user1";
        String password = "pw123";
        LoginRequestDto requestDto = new LoginRequestDto(username, password);

        when(authAdapter.login(requestDto)).thenThrow(FeignException.class);

        LoginResponseDto result = authService.login(username, password);

        assertThat(result).isNull();
    }

    @Test
    void validate_success_returnsTrue() {
        String token = "token";
        when(authAdapter.validate(token)).thenReturn(true);

        boolean result = authService.validate(token);

        assertThat(result).isTrue();
    }

    @Test
    void parse_success_returnsTokenParseResponseDto() {
        String token = "token";
        TokenParseResponseDto responseDto = new TokenParseResponseDto("user1", List.of("ROLE_USER"));
        when(authAdapter.parse(token)).thenReturn(responseDto);

        TokenParseResponseDto result = authService.parse(token);

        assertThat(result).isEqualTo(responseDto);
    }

    @Test
    void refresh_success_returnsRefreshTokenResponseDto() {
        String refreshToken = "refresh-token";
        RefreshTokenResponseDto responseDto = new RefreshTokenResponseDto("access-token", "refresh-token");
        when(authAdapter.refresh(refreshToken)).thenReturn(responseDto);

        RefreshTokenResponseDto result = authService.refresh(refreshToken);

        assertThat(result).isEqualTo(responseDto);
    }
}