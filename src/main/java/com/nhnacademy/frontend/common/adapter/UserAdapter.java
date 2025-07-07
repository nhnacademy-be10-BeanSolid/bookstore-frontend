package com.nhnacademy.frontend.common.adapter;

import com.nhnacademy.frontend.common.adapter.domain.response.ResponseUser;
import com.nhnacademy.frontend.auth.domain.request.UserCreateRequestDto;
import com.nhnacademy.frontend.mypage.domain.request.UserUpdateRequestDto;
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

    @GetMapping("/user-api/users/{userId}")
    ResponseEntity<ResponseUser> getUser(@PathVariable String userId);

    @PutMapping("/user-api/users/me/personalinformation")
    ResponseEntity<ResponseUser> updatePersonalInformation(@RequestBody UserUpdateRequestDto request);

    @GetMapping("/user-api/users/me")
    ResponseEntity<ResponseUser> getUserInfo();
}
