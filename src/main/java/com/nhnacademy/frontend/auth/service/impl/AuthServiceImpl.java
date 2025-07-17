package com.nhnacademy.frontend.auth.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nhnacademy.frontend.auth.adapter.AuthAdapter;
import com.nhnacademy.frontend.auth.dto.request.DormantUserVerificationRequestDto;
import com.nhnacademy.frontend.auth.dto.request.LoginRequestDto;
import com.nhnacademy.frontend.auth.dto.request.NonMemberLoginRequest;
import com.nhnacademy.frontend.auth.dto.request.OAuth2AdditionalSignupRequestDto;
import com.nhnacademy.frontend.auth.dto.request.OAuth2LoginRequestDto;
import com.nhnacademy.frontend.auth.dto.response.*;
import com.nhnacademy.frontend.auth.exception.UserDormantException;
import com.nhnacademy.frontend.auth.service.AuthService;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
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

            if(e.getMessage().contains("휴면")) {
                throw new UserDormantException(e.getMessage());
            }
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

    @Override
    public boolean verifyDormantUserCode(DormantUserVerificationRequestDto dto) {
        try {
            return authAdapter.verifyDormantUserCode(dto);
        } catch (FeignException e) {
            log.error("휴면 사용자 인증 실패: {}", e.getMessage(), e);
            return false;
        }
    }


    @Override
    public boolean nonMemberLogin(NonMemberLoginRequest request) {
        try {
            return authAdapter.nonMemberLogin(request);
        } catch (FeignException e) {
            log.error("비회원 로그인 실패: {}", e.getMessage(), e);
            return false;
        }
    }
}
