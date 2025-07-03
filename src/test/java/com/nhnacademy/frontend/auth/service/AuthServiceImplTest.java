package com.nhnacademy.frontend.auth.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nhnacademy.frontend.adapter.auth.AuthAdapter;
import com.nhnacademy.frontend.auth.domain.request.LoginRequestDto;
import com.nhnacademy.frontend.auth.domain.request.OAuth2AdditionalSignupRequestDto;
import com.nhnacademy.frontend.auth.domain.request.OAuth2LoginRequestDto;
import com.nhnacademy.frontend.auth.domain.response.*;
import com.nhnacademy.frontend.auth.service.impl.AuthServiceImpl;
import feign.FeignException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
class AuthServiceImplTest {

    @Mock
    AuthAdapter authAdapter;

    @Mock
    ObjectMapper objectMapper;

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

    @Test
    void oauth2Login_shouldCallAuthAdapterAndReturnResponseDto() {
        String provider = "payco";
        String code = "testcode";
        ResponseDto<?> expectResponse = ResponseDto.builder()
                .success(true)
                .message("Success")
                .data(null)
                .build();

        doReturn(expectResponse).when(authAdapter).oauth2Login(any(OAuth2LoginRequestDto.class));

        ResponseDto<?> actualResponse = authService.oauth2Login(provider, code);

        verify(authAdapter, times(1)).oauth2Login(any(OAuth2LoginRequestDto.class));
        assertEquals(expectResponse, actualResponse);
    }

    @Test
    void oauth2Login_mobileParsing_shouldSplitMobileNumber() {
        String provider = "provider";
        String code = "code";
        String mobile = "010-1234-5678";
        AdditionalSignupRequiredDto signupData = AdditionalSignupRequiredDto.builder()
                .tempJwt("tempJwt")
                .name("name")
                .email("email")
                .mobile(mobile)
                .build();
        ResponseDto<?> response = ResponseDto.builder()
                .success(false)
                .message("추가 회원가입 필요")
                .data(signupData)
                .build();

        doReturn(response).when(authAdapter).oauth2Login(any(OAuth2LoginRequestDto.class));
        doReturn(signupData).when(objectMapper).convertValue(any(AdditionalSignupRequiredDto.class), eq(AdditionalSignupRequiredDto.class));

        ResponseDto<?> result = authService.oauth2Login(provider, code);

        AdditionalSignupRequiredDto resultData = (AdditionalSignupRequiredDto) result.getData();

        assertThat(resultData.getMobileParts()).containsExactly("010", "1234", "5678");

    }

    @Test
    void oauth2AdditionalSignup_shouldCallAuthAdapterAndReturnOAuth2LoginResponseDto() {
        OAuth2AdditionalSignupRequestDto request = new OAuth2AdditionalSignupRequestDto();
        OAuth2LoginResponseDto expectedResponse = new OAuth2LoginResponseDto("accessToken", "refreshToken");

        when(authAdapter.additionalSignup(any(OAuth2AdditionalSignupRequestDto.class))).thenReturn(expectedResponse);

        OAuth2LoginResponseDto actualResponse = authService.oauth2AdditionalSignup(request);

        verify(authAdapter, times(1)).additionalSignup(any(OAuth2AdditionalSignupRequestDto.class));
        assertEquals(expectedResponse, actualResponse);
    }
}