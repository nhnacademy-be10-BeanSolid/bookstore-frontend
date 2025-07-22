package com.nhnacademy.frontend.admin.adapter;

import com.nhnacademy.frontend.admin.dto.request.CouponPolicyCreateRequest;
import com.nhnacademy.frontend.admin.dto.response.CouponPolicyResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@FeignClient(name = "gateway-service", contextId = "couponAdminAdaptor")
public interface CouponAdminAdaptor {

    @PostMapping("/coupon-api/admin/coupon-policies")
    void createCouponPolicy(@RequestBody CouponPolicyCreateRequest request);

    @GetMapping("/coupon-api/admin/coupon-policies")
    List<CouponPolicyResponse> getAllCouponPolicies();

    @GetMapping("/coupon-api/admin/coupon-policies/{couponId}")
    CouponPolicyResponse getCouponPolicyById(@PathVariable Long couponId);

    @DeleteMapping("/coupon-api/admin/coupon-policies/{couponId}")
    void deleteCouponPolicy(@PathVariable Long couponId);
}
