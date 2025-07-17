package com.nhnacademy.frontend.common.adapter;

import com.nhnacademy.frontend.coupon.dto.UserCouponResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@FeignClient(name = "gateway-service", contextId = "couponAdapter")
public interface CouponAdapter {

    @GetMapping("/coupons/users/{userNo}/active")
    ResponseEntity<List<UserCouponResponse>> getActiveUserCoupons(@PathVariable("userNo") String userNo);
}
