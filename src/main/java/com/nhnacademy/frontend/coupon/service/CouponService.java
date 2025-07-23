package com.nhnacademy.frontend.coupon.service;

import com.nhnacademy.frontend.coupon.dto.CouponPolicyResponse;
import com.nhnacademy.frontend.coupon.dto.UserCouponResponse;

import java.util.List;

public interface CouponService {
    List<UserCouponResponse> getActiveUserCoupons(Long userNo);
    UserCouponResponse issueCouponToUser(Long userNo, Long couponPolicyId);
    List<CouponPolicyResponse> getAllCouponPolicies();
}