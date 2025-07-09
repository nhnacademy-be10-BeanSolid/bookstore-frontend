package com.nhnacademy.frontend.admin.adapter;

import com.nhnacademy.frontend.admin.domain.request.PointTypeCreateRequestDto;
import com.nhnacademy.frontend.admin.domain.response.ResponsePointType;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@FeignClient(name = "gateway-service", contextId = "adminAdapter")
public interface AdminAdapter {

    @GetMapping("/user-api/users/pointType")
    ResponseEntity<Page<ResponsePointType>> getAllPointTypes(@RequestParam int page, @RequestParam int size);

    @PostMapping("/user-api/users/pointType")
    void addPointType(@RequestBody PointTypeCreateRequestDto pointTypeCreateRequestDto);

    @DeleteMapping("/user-api/users/pointType/{typeId}")
    void deletePointType(@PathVariable("typeId") Long typeId);
}
