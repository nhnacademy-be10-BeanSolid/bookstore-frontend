package com.nhnacademy.frontend.common.adapter;

import com.nhnacademy.dto.CouponPolicyResponseDto;
import com.nhnacademy.frontend.coupon.dto.UserCouponResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient(name = "gateway-service", contextId = "couponAdapter")
public interface CouponAdapter {

    @GetMapping("/coupon-api/coupons/users/{userNo}/active")
    ResponseEntity<List<UserCouponResponse>> getActiveUserCoupons(@PathVariable("userNo") Long userNo);

    @GetMapping("/coupon-api/coupons/policy")
    List<CouponPolicyResponseDto> getAllCouponPolicies();

    @PostMapping("/coupon-api/admin/issue-all/{couponPolicyId}")
    void startIssuingCouponsToAllUsers(@PathVariable("couponPolicyId") Long couponPolicyId);

    @PostMapping("/coupon-api/admin/issue-book")
    void issueCouponToBook(@RequestParam("couponPolicyId") Long couponPolicyId, @RequestParam("bookId") Long bookId);

    @PostMapping("/coupon-api/admin/issue-to-user")
    void issueCouponToUser(@RequestParam("userNo") Long userNo, @RequestParam("couponPolicyId") Long couponPolicyId);

    @PostMapping("/coupon-api/coupons/issue/book")
    ResponseEntity<com.nhnacademy.frontend.coupon.dto.UserCouponResponse> issueBookCoupon(com.nhnacademy.dto.IssueBookCouponRequest request);
}
