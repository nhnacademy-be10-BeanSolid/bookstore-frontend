package com.nhnacademy.frontend.coupon.dto;

import com.nhnacademy.frontend.coupon.domain.CouponDiscountType;
import com.nhnacademy.frontend.coupon.domain.CouponScope;
import com.nhnacademy.frontend.coupon.domain.CouponType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class CouponPolicyResponse {
    private Long couponId;
    private String couponName;
    private CouponDiscountType couponDiscountType;
    private int couponDiscountAmount;
    private Integer couponMinimumOrderAmount;
    private Integer couponMaximumDiscountAmount;
    private CouponScope couponScope;
    private LocalDateTime couponExpiredAt;
    private Integer couponIssuePeriod;
    private CouponType couponType;
    private LocalDateTime couponCreatedAt;
    private List<Long> bookIds;
    private List<Long> categoryIds;
}
