package com.nhnacademy.frontend.auth.service.impl;

import com.nhnacademy.frontend.adapter.AuthAdapter;
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
}
