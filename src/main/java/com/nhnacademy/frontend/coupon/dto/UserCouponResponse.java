package com.nhnacademy.frontend.coupon.dto;


import com.nhnacademy.frontend.coupon.domain.UserCouponStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserCouponResponse {
    private Long userCouponId;
    private Long userNo;
    private Long couponPolicyId;
    private String couponName;
    private int couponDiscountAmount;

    private LocalDateTime issuedAt;

    private LocalDateTime expiredAt;

    private LocalDateTime usedAt;

    private UserCouponStatus status;
    private Long orderId;
}
