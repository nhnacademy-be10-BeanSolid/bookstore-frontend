package com.nhnacademy.frontend.common.adapter;

import com.nhnacademy.frontend.user.domain.request.UserCreateRequestDto;
import com.nhnacademy.frontend.user.domain.response.UserResponseDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@FeignClient(name = "gateway-service", contextId = "userAdapter")
public interface UserAdapter {
    @PostMapping("/user-api/users/register")
    void registerUser(@RequestBody UserCreateRequestDto request);

    @GetMapping("/user-api/users/check-userId")
    boolean isExistUser(@RequestParam String userId);

    @GetMapping("/user-api/users/{userId}")
    ResponseEntity<UserResponseDto> getUser(@PathVariable String userId);
}
