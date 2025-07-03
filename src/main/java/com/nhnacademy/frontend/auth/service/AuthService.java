package com.nhnacademy.frontend.auth.service;

import com.nhnacademy.frontend.auth.domain.request.OAuth2AdditionalSignupRequestDto;
import com.nhnacademy.frontend.auth.domain.response.*;

public interface AuthService {
    LoginResponseDto login(String username, String password);

    boolean validate(String token);

    TokenParseResponseDto parse(String token);

    RefreshTokenResponseDto refresh(String refreshToken);

    ResponseDto<?> oauth2Login(String provider, String code);

    OAuth2LoginResponseDto oauth2AdditionalSignup(OAuth2AdditionalSignupRequestDto request);
}
