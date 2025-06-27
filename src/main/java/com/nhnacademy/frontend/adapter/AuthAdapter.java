package com.nhnacademy.frontend.adapter;

import com.nhnacademy.frontend.auth.domain.*;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "gateway-service")
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
    LoginResponseDto oauth2Login(@RequestBody OAuth2LoginRequestDto request);
}
