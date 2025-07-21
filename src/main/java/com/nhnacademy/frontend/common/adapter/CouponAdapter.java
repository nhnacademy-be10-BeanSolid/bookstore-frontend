package com.nhnacademy.frontend.common.adapter;

import com.nhnacademy.frontend.coupon.dto.IssueBookCouponRequest;
import com.nhnacademy.frontend.coupon.dto.CouponPolicyResponse;
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
    List<CouponPolicyResponse> getAllCouponPolicies();

    @PostMapping("/coupon-api/admin/issue-all/{couponPolicyId}")
    void startIssuingCouponsToAllUsers(@PathVariable("couponPolicyId") Long couponPolicyId);

    @PostMapping("/coupon-api/admin/issue-book")
    void issueCouponToBook(@RequestParam("couponPolicyId") Long couponPolicyId, @RequestParam("bookId") Long bookId);

    @PostMapping("/coupon-api/coupons/users/{userNo}/issue/{couponPolicyId}")
    ResponseEntity<com.nhnacademy.frontend.coupon.dto.UserCouponResponse> issueCouponToUser(@PathVariable("userNo") Long userNo, @PathVariable("couponPolicyId") Long couponPolicyId);

    @PostMapping("/coupon-api/coupons/issue/book")
    ResponseEntity<com.nhnacademy.frontend.coupon.dto.UserCouponResponse> issueBookCoupon(IssueBookCouponRequest request);
}
