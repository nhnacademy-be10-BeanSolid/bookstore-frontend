package com.nhnacademy.frontend.admin.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CouponPolicyResponse {
    private Long couponId;
    private String couponName;
    private String couponDiscountType;
    private Integer couponDiscountAmount;
    private Integer couponMinimumOrderAmount;
    private Integer couponMaximumDiscountAmount;
    private String couponScope;
    private LocalDateTime couponExpiredAt;
    private Integer couponIssuePeriod;
    private String couponType;
    private LocalDateTime couponCreatedAt;
    private List<Long> bookIds;
    private List<Long> categoryIds;
}
