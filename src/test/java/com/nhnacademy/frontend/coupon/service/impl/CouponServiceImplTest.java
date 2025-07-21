package com.nhnacademy.frontend.coupon.service.impl;

import com.nhnacademy.frontend.common.adapter.CouponAdapter;
import com.nhnacademy.frontend.common.adapter.UserAdapter;
import com.nhnacademy.frontend.common.adapter.dto.user.response.ResponseUser;
import com.nhnacademy.frontend.coupon.domain.UserCouponStatus;
import com.nhnacademy.frontend.coupon.dto.UserCouponResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CouponServiceImplTest {

    @Mock
    private CouponAdapter couponAdapter;

    @Mock
    private UserAdapter userAdapter;

    @InjectMocks
    private CouponServiceImpl couponService;

    @Test
    void getActiveUserCoupons() {
        String userId = "testUser";
        Long userNo = 1L;

        ResponseUser mockUser = new ResponseUser(userNo, "testUser", "password", "nickname", "010-1234-5678", "test@test.com", LocalDate.now(), 1000, false, null, null, null);
        ResponseEntity<ResponseUser> userResponseEntity = ResponseEntity.ok(mockUser);

        UserCouponResponse coupon1 = UserCouponResponse.builder()
                .userCouponId(1L)
                .userNo(String.valueOf(userNo))
                .couponPolicyId(1L)
                .couponName("Coupon1")
                .couponDiscountAmount(1000)
                .issuedAt(LocalDateTime.now())
                .expiredAt(LocalDateTime.now().plusDays(7))
                .usedAt(null)
                .status(UserCouponStatus.ACTIVE)
                .orderId(null)
                .build();

        UserCouponResponse coupon2 = UserCouponResponse.builder()
                .userCouponId(2L)
                .userNo(String.valueOf(userNo))
                .couponPolicyId(2L)
                .couponName("Coupon2")
                .couponDiscountAmount(10)
                .issuedAt(LocalDateTime.now())
                .expiredAt(LocalDateTime.now().plusDays(14))
                .usedAt(null)
                .status(UserCouponStatus.ACTIVE)
                .orderId(null)
                .build();

        List<UserCouponResponse> expectedCoupons = Arrays.asList(coupon1, coupon2);
        ResponseEntity<List<UserCouponResponse>> couponResponseEntity = ResponseEntity.ok(expectedCoupons);

        when(userAdapter.getUser(userId)).thenReturn(userResponseEntity);
        when(couponAdapter.getActiveUserCoupons(String.valueOf(userNo))).thenReturn(couponResponseEntity);

        List<UserCouponResponse> actualCoupons = couponService.getActiveUserCoupons(userId);

        assertEquals(expectedCoupons.size(), actualCoupons.size());
        assertEquals(expectedCoupons.get(0).getCouponName(), actualCoupons.get(0).getCouponName());
        assertEquals(expectedCoupons.get(1).getCouponName(), actualCoupons.get(1).getCouponName());
    }
}