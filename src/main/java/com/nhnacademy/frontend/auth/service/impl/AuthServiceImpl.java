package com.nhnacademy.frontend.auth.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nhnacademy.frontend.adapter.AuthAdapter;
import com.nhnacademy.frontend.auth.domain.request.LoginRequestDto;
import com.nhnacademy.frontend.auth.domain.request.OAuth2AdditionalSignupRequestDto;
import com.nhnacademy.frontend.auth.domain.request.OAuth2LoginRequestDto;
import com.nhnacademy.frontend.auth.domain.response.*;
import com.nhnacademy.frontend.auth.domain.*;
import com.nhnacademy.frontend.auth.service.AuthService;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {
    private final AuthAdapter authAdapter;
    private final ObjectMapper objectMapper;

    @Override
    public LoginResponseDto login(String username, String password) {
        try {
            LoginRequestDto request = new LoginRequestDto(username, password);
            return authAdapter.login(request);
        } catch (FeignException e) {
            log.error("Login failed for user {}: {}", username, e.getMessage(), e);
            return null;
        }
    }

    @Override
    public boolean validate(String token) {
        return authAdapter.validate(token);
    }

    @Override
    public TokenParseResponseDto parse(String token) {
        return authAdapter.parse(token);
    }

    @Override
    public RefreshTokenResponseDto refresh(String refreshToken) {
        return authAdapter.refresh(refreshToken);
    }

    @Override
    public ResponseDto<?> oauth2Login(String provider, String code) {
        ResponseDto<?> result = authAdapter.oauth2Login(new OAuth2LoginRequestDto(provider, code));

        if(!result.isSuccess() && result.getData() instanceof AdditionalSignupRequiredDto) {
            AdditionalSignupRequiredDto signupData = objectMapper.convertValue(result.getData(), AdditionalSignupRequiredDto.class);
            if(signupData.getMobile() != null && signupData.getMobile().length() >= 11 && signupData.getMobile().contains("-")) {
                String[] parts = signupData.getMobile().split("-");
                signupData = AdditionalSignupRequiredDto.builder()
                        .tempJwt(signupData.getTempJwt())
                        .name(signupData.getName())
                        .email(signupData.getEmail())
                        .mobile(signupData.getMobile())
                        .mobileParts(parts)
                        .build();
            }
            result = ResponseDto.builder()
                    .success(result.isSuccess())
                    .message(result.getMessage())
                    .data(signupData)
                    .build();
        }
        return result;
    }

    @Override
    public OAuth2LoginResponseDto oauth2AdditionalSignup(OAuth2AdditionalSignupRequestDto request) {
        return authAdapter.additionalSignup(request);
    }
}
