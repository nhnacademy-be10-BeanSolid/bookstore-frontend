package com.nhnacademy.frontend.coupon.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class IssueCategoryCouponRequest {
    private Long userId;
    private Long categoryId;
    private Long couponPolicyId;
}
