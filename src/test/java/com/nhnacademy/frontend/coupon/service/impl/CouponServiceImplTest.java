package com.nhnacademy.frontend.coupon.service.impl;

import com.nhnacademy.frontend.common.adapter.CouponAdapter;
import com.nhnacademy.frontend.coupon.domain.CouponDiscountType;
import com.nhnacademy.frontend.coupon.domain.UserCouponStatus;
import com.nhnacademy.frontend.coupon.dto.CouponPolicyResponse;
import com.nhnacademy.frontend.coupon.dto.UserCouponResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CouponServiceImplTest {

    @Mock
    private CouponAdapter couponAdapter;

    @InjectMocks
    private CouponServiceImpl couponService;

    private Long testUserNo;
    private Long testCouponPolicyId;
    private List<UserCouponResponse> mockActiveUserCoupons;
    private List<CouponPolicyResponse> mockAllCouponPolicies;

    @BeforeEach
    void setUp() {
        testUserNo = 1L;
        testCouponPolicyId = 10L;

        mockActiveUserCoupons = Arrays.asList(
                new UserCouponResponse(1L, testUserNo, 1L, "Test Coupon 1", 1000, LocalDateTime.now(), LocalDateTime.now().plusDays(30), null, UserCouponStatus.ACTIVE, null),
                new UserCouponResponse(2L, testUserNo, 2L, "Test Coupon 2", 10, LocalDateTime.now(), LocalDateTime.now().plusDays(60), null, UserCouponStatus.ACTIVE, null)
        );

        mockAllCouponPolicies = Arrays.asList(
                new CouponPolicyResponse(1L, "Policy 1", CouponDiscountType.AMOUNT, 1000, null, null, null, null, null, null, null, null, null),
                new CouponPolicyResponse(2L, "Policy 2", CouponDiscountType.PERCENT, 10, null, null, null, null, null, null, null, null, null)
        );
    }

    @Test
    void getActiveUserCoupons_shouldReturnListOfUserCoupons() {
        when(couponAdapter.getActiveUserCoupons(testUserNo)).thenReturn(ResponseEntity.ok(mockActiveUserCoupons));

        List<UserCouponResponse> result = couponService.getActiveUserCoupons(testUserNo);

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("Test Coupon 1", result.get(0).getCouponName());
        verify(couponAdapter, times(1)).getActiveUserCoupons(testUserNo);
    }

    @Test
    void getActiveUserCoupons_shouldReturnEmptyListWhenNoCoupons() {
        when(couponAdapter.getActiveUserCoupons(testUserNo)).thenReturn(ResponseEntity.ok(Collections.emptyList()));

        List<UserCouponResponse> result = couponService.getActiveUserCoupons(testUserNo);

        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(couponAdapter, times(1)).getActiveUserCoupons(testUserNo);
    }

    @Test
    void issueCouponToUser_shouldReturnUserCouponResponse() {
        UserCouponResponse expectedResponse = new UserCouponResponse(3L, testUserNo, testCouponPolicyId, "Issued Coupon", 500, LocalDateTime.now(), LocalDateTime.now().plusDays(30), null, UserCouponStatus.ACTIVE, null);
        when(couponAdapter.issueCouponToUser(testUserNo, testCouponPolicyId)).thenReturn(ResponseEntity.ok(expectedResponse));

        UserCouponResponse result = couponService.issueCouponToUser(testUserNo, testCouponPolicyId);

        assertNotNull(result);
        assertEquals(expectedResponse.getCouponName(), result.getCouponName());
        verify(couponAdapter, times(1)).issueCouponToUser(testUserNo, testCouponPolicyId);
    }

    @Test
    void getAllCouponPolicies_shouldReturnListOfCouponPolicies() {
        when(couponAdapter.getAllCouponPolicies()).thenReturn(mockAllCouponPolicies);

        List<CouponPolicyResponse> result = couponService.getAllCouponPolicies();

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("Policy 1", result.get(0).getCouponName());
        verify(couponAdapter, times(1)).getAllCouponPolicies();
    }

    @Test
    void getAllCouponPolicies_shouldReturnEmptyListWhenNoPolicies() {
        when(couponAdapter.getAllCouponPolicies()).thenReturn(Collections.emptyList());

        List<CouponPolicyResponse> result = couponService.getAllCouponPolicies();

        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(couponAdapter, times(1)).getAllCouponPolicies();
    }
}
