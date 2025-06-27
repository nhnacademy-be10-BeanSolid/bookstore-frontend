package com.nhnacademy.frontend.auth.service;

import com.nhnacademy.frontend.auth.domain.LoginResponseDto;
import com.nhnacademy.frontend.auth.domain.RefreshTokenResponseDto;
import com.nhnacademy.frontend.auth.domain.TokenParseResponseDto;

public interface AuthService {
    LoginResponseDto login(String username, String password);

    boolean validate(String token);

    TokenParseResponseDto parse(String token);

    RefreshTokenResponseDto refresh(String refreshToken);

    LoginResponseDto oauth2Login(String provider, String code);
}
