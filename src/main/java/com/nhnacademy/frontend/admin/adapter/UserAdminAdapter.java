package com.nhnacademy.frontend.admin.adapter;

import com.nhnacademy.frontend.common.adapter.dto.user.request.PointTypeCreateRequestDto;
import com.nhnacademy.frontend.common.adapter.dto.user.request.PointTypeUpdateRequestDto;
import com.nhnacademy.frontend.common.adapter.dto.user.response.ResponsePointType;
import com.nhnacademy.frontend.common.adapter.dto.user.response.ResponseUser;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@FeignClient(name = "gateway-service", contextId = "userAdminAdapter")
public interface UserAdminAdapter {

    @GetMapping("/user-api/users/pointType")
    ResponseEntity<Page<ResponsePointType>> getAllPointTypes(@RequestParam int page, @RequestParam int size);

    @PostMapping("/user-api/users/pointType")
    void addPointType(@RequestBody PointTypeCreateRequestDto requestDto);

    @DeleteMapping("/user-api/users/pointType/{typeId}")
    void deletePointType(@PathVariable("typeId") Long typeId);

    @PutMapping("/user-api/users/pointType/{typeId}/isActive")
    void changeIsActivePointType(@PathVariable("typeId") Long typeId);

    @PutMapping("/user-api/users/pointType/{typeId}/edit")
    void editPointType(@RequestBody PointTypeUpdateRequestDto requestDto, @PathVariable("typeId") Long typeId);

    @GetMapping("/user-api/users/pointType/{typeId}")
    ResponseEntity<ResponsePointType> getPointType(@PathVariable("typeId") Long typeId);

    @GetMapping("/user-api/users")
    ResponseEntity<Page<ResponseUser>> getAllUsers(@RequestParam int page, @RequestParam int size);

    @GetMapping("/user-api/users/bulk/status")
    ResponseEntity<Void> bulkUpdateUserStatus();
}
