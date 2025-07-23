package com.nhnacademy.frontend.coupon.service.impl;

import com.nhnacademy.frontend.common.adapter.CouponAdapter;
import com.nhnacademy.frontend.common.adapter.UserAdapter;
import com.nhnacademy.frontend.common.adapter.dto.user.response.ResponseUser;
import com.nhnacademy.frontend.coupon.domain.UserCouponStatus;
import com.nhnacademy.frontend.coupon.dto.UserCouponResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CouponServiceImplTest {

    @Mock
    private CouponAdapter couponAdapter;

    @Mock
    private UserAdapter userAdapter;

    @InjectMocks
    private CouponServiceImpl couponService;

    private final String userId = "testUser";
    private final Long userNo = 1L;
    private final ResponseUser mockUser = new ResponseUser(userNo, "testUser", "password", "nickname", "010-1234-5678", "test@test.com", LocalDate.now(), 1000, false, null, LocalDateTime.now(), null, null);

    private UserCouponResponse createCoupon(Long userCouponId, String couponName, int discountAmount) {
        return UserCouponResponse.builder()
                .userCouponId(userCouponId)
                .userNo(userNo)
                .couponPolicyId(userCouponId)
                .couponName(couponName)
                .couponDiscountAmount(discountAmount)
                .issuedAt(LocalDateTime.now())
                .expiredAt(LocalDateTime.now().plusDays(7))
                .usedAt(null)
                .status(UserCouponStatus.ACTIVE)
                .orderId(null)
                .build();
    }

    @Test
    @DisplayName("활성 사용자 쿠폰 조회 - 성공")
    void getActiveUserCoupons_success() {
        UserCouponResponse coupon1 = createCoupon(1L, "Coupon1", 1000);
        UserCouponResponse coupon2 = createCoupon(2L, "Coupon2", 10);
        List<UserCouponResponse> expectedCoupons = Arrays.asList(coupon1, coupon2);

        when(userAdapter.getUser(userId)).thenReturn(ResponseEntity.ok(mockUser));
        when(couponAdapter.getActiveUserCoupons(userNo)).thenReturn(ResponseEntity.ok(expectedCoupons));

        List<UserCouponResponse> actualCoupons = couponService.getActiveUserCoupons(userId);

        assertNotNull(actualCoupons);
        assertEquals(expectedCoupons.size(), actualCoupons.size());
        assertEquals(expectedCoupons.get(0).getCouponName(), actualCoupons.get(0).getCouponName());
        assertEquals(expectedCoupons.get(1).getCouponName(), actualCoupons.get(1).getCouponName());
    }

    @Test
    @DisplayName("활성 사용자 쿠폰 조회 - 사용자 없음")
    void getActiveUserCoupons_userNotFound() {
        when(userAdapter.getUser(userId)).thenReturn(ResponseEntity.notFound().build());

        List<UserCouponResponse> actualCoupons = couponService.getActiveUserCoupons(userId);

        assertNotNull(actualCoupons);
        assertTrue(actualCoupons.isEmpty());
    }

    @Test
    @DisplayName("활성 사용자 쿠폰 조회 - 쿠폰 없음")
    void getActiveUserCoupons_noCouponsFound() {
        when(userAdapter.getUser(userId)).thenReturn(ResponseEntity.ok(mockUser));
        when(couponAdapter.getActiveUserCoupons(userNo)).thenReturn(ResponseEntity.ok(Collections.emptyList()));

        List<UserCouponResponse> actualCoupons = couponService.getActiveUserCoupons(userId);

        assertNotNull(actualCoupons);
        assertTrue(actualCoupons.isEmpty());
    }
}