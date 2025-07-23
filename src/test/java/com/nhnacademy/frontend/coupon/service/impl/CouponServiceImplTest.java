package com.nhnacademy.frontend.coupon.service.impl;

import com.nhnacademy.frontend.common.adapter.CouponAdapter;
import com.nhnacademy.frontend.coupon.dto.CouponPolicyResponse;
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
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CouponServiceImplTest {

    @Mock
    private CouponAdapter couponAdapter;

    @InjectMocks
    private CouponServiceImpl couponService;

    private Long testUserNo;
    private Long testCouponPolicyId;
    private UserCouponResponse testUserCouponResponse;
    private CouponPolicyResponse testCouponPolicyResponse;

    @BeforeEach
    void setUp() {
        testUserNo = 1L;
        testCouponPolicyId = 10L;
        testUserCouponResponse = UserCouponResponse.builder()
            .userCouponId(1L)
            .userNo(testUserNo)
            .couponPolicyId(testCouponPolicyId)
            .couponName("Test Coupon")
            .couponDiscountAmount(1000)
            .issuedAt(LocalDateTime.now())
            .expiredAt(LocalDateTime.now().plusDays(7))
            .build();

        testCouponPolicyResponse = CouponPolicyResponse.builder()
            .couponId(testCouponPolicyId)
            .couponName("Test Policy")
            .couponDiscountAmount(1000)
            .build();
    }

    @Test
    @DisplayName("활성 사용자 쿠폰 조회 - 성공")
    void getActiveUserCoupons_success() {
        when(couponAdapter.getActiveUserCoupons(anyLong()))
            .thenReturn(ResponseEntity.ok(List.of(testUserCouponResponse)));

        List<UserCouponResponse> result = couponService.getActiveUserCoupons(testUserNo);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(testUserCouponResponse.getCouponName(), result.get(0).getCouponName());
    }

    @Test
    @DisplayName("활성 사용자 쿠폰 조회 - 결과 없음")
    void getActiveUserCoupons_noResult() {
        when(couponAdapter.getActiveUserCoupons(anyLong()))
            .thenReturn(ResponseEntity.ok(Collections.emptyList()));

        List<UserCouponResponse> result = couponService.getActiveUserCoupons(testUserNo);

        assertNotNull(result);
        assertEquals(0, result.size());
    }

    @Test
    @DisplayName("사용자에게 쿠폰 발급 - 성공")
    void issueCouponToUser_success() {
        when(couponAdapter.issueCouponToUser(anyLong(), anyLong()))
            .thenReturn(ResponseEntity.ok(testUserCouponResponse));

        UserCouponResponse result = couponService.issueCouponToUser(testUserNo, testCouponPolicyId);

        assertNotNull(result);
        assertEquals(testUserCouponResponse.getCouponName(), result.getCouponName());
    }

    @Test
    @DisplayName("사용자에게 쿠폰 발급 - 실패 (어댑터 오류)")
    void issueCouponToUser_failure() {
        when(couponAdapter.issueCouponToUser(anyLong(), anyLong()))
            .thenThrow(new RuntimeException("Adapter error"));

        assertThrows(RuntimeException.class, () -> couponService.issueCouponToUser(testUserNo, testCouponPolicyId));
    }

    @Test
    @DisplayName("모든 쿠폰 정책 조회 - 성공")
    void getAllCouponPolicies_success() {
        when(couponAdapter.getAllCouponPolicies())
            .thenReturn(List.of(testCouponPolicyResponse));

        List<CouponPolicyResponse> result = couponService.getAllCouponPolicies();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(testCouponPolicyResponse.getCouponName(), result.get(0).getCouponName());
    }

    @Test
    @DisplayName("모든 쿠폰 정책 조회 - 결과 없음")
    void getAllCouponPolicies_noResult() {
        when(couponAdapter.getAllCouponPolicies())
            .thenReturn(Collections.emptyList());

        List<CouponPolicyResponse> result = couponService.getAllCouponPolicies();

        assertNotNull(result);
        assertEquals(0, result.size());
    }
}