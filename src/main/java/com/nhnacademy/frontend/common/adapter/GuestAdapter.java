package com.nhnacademy.frontend.common.adapter;

import com.nhnacademy.frontend.common.adapter.dto.user.request.GuestCreateRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "gateway-service", contextId = "guestAdapter")
public interface GuestAdapter {

    @PostMapping("/user-api/guests")
    void registerGuest(@RequestBody GuestCreateRequest request);
}
