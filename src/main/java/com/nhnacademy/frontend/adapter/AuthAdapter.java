package com.nhnacademy.frontend.adapter;

import com.nhnacademy.frontend.domain.LoginRequestDto;
import com.nhnacademy.frontend.domain.LoginResponseDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "gateway-service")
public interface AuthAdapter {
    @PostMapping("/auth-service/auth/login")
    LoginResponseDto login(@RequestBody LoginRequestDto request);
}
