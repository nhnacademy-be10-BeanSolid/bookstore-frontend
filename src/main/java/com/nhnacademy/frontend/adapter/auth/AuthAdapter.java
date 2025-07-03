package com.nhnacademy.frontend.adapter.auth;

import com.nhnacademy.frontend.auth.domain.request.LoginRequestDto;
import com.nhnacademy.frontend.auth.domain.request.OAuth2AdditionalSignupRequestDto;
import com.nhnacademy.frontend.auth.domain.request.OAuth2LoginRequestDto;
import com.nhnacademy.frontend.auth.domain.response.*;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "gateway-service", contextId = "authAdapter")
public interface AuthAdapter {
    @PostMapping("/auth-service/auth/login")
    LoginResponseDto login(@RequestBody LoginRequestDto request);

    @PostMapping("/auth-service/auth/refresh")
    RefreshTokenResponseDto refresh(@RequestBody String request);

    @PostMapping("/auth-service/auth/validate")
    Boolean validate(@RequestBody String token);

    @PostMapping("/auth-service/auth/parse")
    TokenParseResponseDto parse(@RequestBody String token);

    @PostMapping("/auth-service/oauth2/login")
    ResponseDto<?> oauth2Login(@RequestBody OAuth2LoginRequestDto request);

    @PostMapping("/auth-service/oauth2/signup")
    OAuth2LoginResponseDto additionalSignup(@RequestBody OAuth2AdditionalSignupRequestDto request);


}
