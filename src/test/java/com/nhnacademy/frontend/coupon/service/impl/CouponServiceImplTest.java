package com.nhnacademy.frontend.coupon.service.impl;

import com.nhnacademy.frontend.common.adapter.CouponAdapter;
import com.nhnacademy.frontend.coupon.dto.CouponPolicyResponse;
import com.nhnacademy.frontend.coupon.dto.IssueCategoryCouponRequest;
import com.nhnacademy.frontend.coupon.dto.UserCouponResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CouponServiceImplTest {

    @Mock
    private CouponAdapter couponAdapter;

    @InjectMocks
    private CouponServiceImpl couponService;

    private UserCouponResponse testUserCouponResponse;
    private CouponPolicyResponse testCouponPolicyResponse;

    @BeforeEach
    void setUp() {
        testUserCouponResponse = UserCouponResponse.builder()
                .userCouponId(1L)
                .couponName("Test User Coupon")
                .couponDiscountAmount(1000)
                .expiredAt(LocalDateTime.now().plusDays(7))
                .build();

        testCouponPolicyResponse = CouponPolicyResponse.builder()
                .couponId(1L)
                .couponName("Test Coupon Policy")
                .build();
    }

    @Test
    @DisplayName("활성 사용자 쿠폰 조회 - 성공")
    void getActiveUserCoupons_success() {
        when(couponAdapter.getActiveUserCoupons(anyLong()))
                .thenReturn(ResponseEntity.ok(List.of(testUserCouponResponse)));

        List<UserCouponResponse> result = couponService.getActiveUserCoupons(1L);

        assertThat(result).isNotNull().hasSize(1);
        assertThat(result.get(0).getUserCouponId()).isEqualTo(1L);
    }

    @Test
    @DisplayName("사용자에게 쿠폰 발급 - 성공")
    void issueCouponToUser_success() {
        when(couponAdapter.issueCouponToUser(anyLong(), anyLong()))
                .thenReturn(ResponseEntity.ok(testUserCouponResponse));

        UserCouponResponse result = couponService.issueCouponToUser(1L, 1L);

        assertThat(result).isNotNull();
        assertThat(result.getUserCouponId()).isEqualTo(1L);
    }

    @Test
    @DisplayName("모든 쿠폰 정책 조회 - 성공")
    void getAllCouponPolicies_success() {
        when(couponAdapter.getAllCouponPolicies())
                .thenReturn(List.of(testCouponPolicyResponse));

        List<CouponPolicyResponse> result = couponService.getAllCouponPolicies();

        assertThat(result).isNotNull().hasSize(1);
        assertThat(result.get(0).getCouponId()).isEqualTo(1L);
    }

    @Test
    @DisplayName("카테고리 쿠폰 발급 - 성공")
    void issueCategoryCoupon_success() {
        when(couponAdapter.issueCategoryCoupon(any(IssueCategoryCouponRequest.class)))
                .thenReturn(ResponseEntity.ok(testUserCouponResponse));

        UserCouponResponse result = couponService.issueCategoryCoupon(1L, 1L, 10L);

        assertThat(result).isNotNull();
        assertThat(result.getUserCouponId()).isEqualTo(1L);
    }
}
