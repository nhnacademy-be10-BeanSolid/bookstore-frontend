package com.nhnacademy.frontend.common.adapter;

import com.nhnacademy.frontend.common.adapter.dto.book.response.BookLikeResponse;
import com.nhnacademy.frontend.common.adapter.dto.user.request.AddressCreateRequest;
import com.nhnacademy.frontend.common.adapter.dto.user.request.UserCreateRequestDto;
import com.nhnacademy.frontend.common.adapter.dto.user.request.UserUpdateRequestDto;
import com.nhnacademy.frontend.common.adapter.dto.user.response.ResponseAddress;
import com.nhnacademy.frontend.common.adapter.dto.user.response.ResponsePoint;
import com.nhnacademy.frontend.common.adapter.dto.user.response.ResponsePointType;
import com.nhnacademy.frontend.common.adapter.dto.user.response.ResponseUser;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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

    @GetMapping("/user-api/users/me/address")
    ResponseEntity<List<ResponseAddress>> getAllAddresses();

    @PostMapping("/user-api/users/me/address")
    ResponseEntity<ResponseAddress> addAddress(@RequestBody AddressCreateRequest address);

    @DeleteMapping("/user-api/users/me/address/{addressId}")
    ResponseEntity<Void> deleteAddress(@PathVariable long addressId);

    @GetMapping("/user-api/users/me/point")
    ResponseEntity<Page<ResponsePoint>> getAllPoints(@RequestParam int page, @RequestParam int size);

    @GetMapping("/user-api/users/pointType")
    ResponseEntity<Page<ResponsePointType>> getPointTypeByGradeName(
            @RequestParam(name = "gradeName", required = false) String gradeName,
            Pageable pageable);

    @PutMapping("/user-api/users/bulk/grade")
    ResponseEntity<Void> bulkUpdateUserGrades();

    @GetMapping("/book-api/users")
    ResponseEntity<Page<BookLikeResponse>> getBookLikes(Pageable pageable);
}
