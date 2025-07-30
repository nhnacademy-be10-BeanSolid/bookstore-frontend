package com.nhnacademy.frontend.coupon.service.impl;

import com.nhnacademy.frontend.common.adapter.CouponAdapter;
import com.nhnacademy.frontend.coupon.dto.CouponPolicyResponse;
import com.nhnacademy.frontend.coupon.dto.IssueCategoryCouponRequest;
import com.nhnacademy.frontend.coupon.dto.UserCouponResponse;
import com.nhnacademy.frontend.coupon.service.CouponService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class CouponServiceImpl implements CouponService {

    private final CouponAdapter couponAdapter;

    @Override
    public List<UserCouponResponse> getActiveUserCoupons(Long userNo) {
        return couponAdapter.getActiveUserCoupons(userNo).getBody();
    }

    @Override
    public UserCouponResponse issueCouponToUser(Long userNo, Long couponPolicyId) {
        return couponAdapter.issueCouponToUser(userNo, couponPolicyId).getBody();
    }

    @Override
    public List<CouponPolicyResponse> getAllCouponPolicies() {
        return couponAdapter.getAllCouponPolicies();
    }

    @Override
    public UserCouponResponse issueCategoryCoupon(Long userNo, Long couponPolicyId, Long categoryId) {
        return couponAdapter.issueCategoryCoupon(
                IssueCategoryCouponRequest.builder()
                        .userId(userNo)
                        .couponPolicyId(couponPolicyId)
                        .categoryId(categoryId)
                        .build()
        ).getBody();
    }
}