package com.nhnacademy.frontend.adapter.user;

import com.nhnacademy.frontend.adapter.user.domain.response.ResponseUser;
import com.nhnacademy.frontend.user.domain.UserCreateRequestDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@FeignClient(name = "gateway-service", contextId = "userAdapter")
public interface UserAdapter {
    @PostMapping("/user-api/users/register")
    void registerUser(@RequestBody UserCreateRequestDto request);

    @GetMapping("/user-api/users/check-userId")
    boolean isExistUser(@RequestParam String userId);

    @PutMapping("/user-api/users/me/status/WITHDRAWN")
    ResponseEntity<ResponseUser> deleteUser();
}
