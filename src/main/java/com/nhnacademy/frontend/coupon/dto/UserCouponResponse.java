package com.nhnacademy.frontend.coupon.dto;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.nhnacademy.frontend.common.util.LocalDateTimeDeserializer;
import com.nhnacademy.frontend.common.util.LocalDateTimeSerializer;
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
    private String userNo;
    private Long couponPolicyId;
    private String couponName;
    private int couponDiscountAmount; // 추가

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSSSS")
    private LocalDateTime issuedAt;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime expiredAt;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime usedAt;

    private UserCouponStatus status;
    private Long orderId;
}
