package com.nhnacademy.frontend.coupon.service.impl;

import com.nhnacademy.frontend.common.adapter.CouponAdapter;
import com.nhnacademy.frontend.coupon.dto.UserCouponResponse;
import com.nhnacademy.frontend.coupon.service.CouponService;
import com.nhnacademy.frontend.common.adapter.UserAdapter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class CouponServiceImpl implements CouponService {

    private final CouponAdapter couponAdapter;
    private final UserAdapter userAdapter;

    @Override
    public List<UserCouponResponse> getActiveUserCoupons(String userId) {
        // userId를 userNo로 변환
        Long userNo = userAdapter.getUser(userId).getBody().getUserNo();
        log.info("CouponServiceImpl: userNo received from UserAdapter: {}", userNo);
        return couponAdapter.getActiveUserCoupons(userNo).getBody();
    }
}
