package com.nhnacademy.frontend.order.adapter;

import com.nhnacademy.frontend.order.dto.request.GuestCreateRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "gateway-service", contextId = "guestAdapter")
public interface GuestAdapter {

    @PostMapping("/order-api/guests")
    void registerGuest(@RequestBody GuestCreateRequest request);
}
