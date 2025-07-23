package com.nhnacademy.frontend.coupon.service.impl;

import com.nhnacademy.frontend.common.adapter.CouponAdapter;
import com.nhnacademy.frontend.common.adapter.UserAdapter;
import com.nhnacademy.frontend.common.adapter.dto.user.response.ResponseUser;
import com.nhnacademy.frontend.coupon.dto.UserCouponResponse;
import com.nhnacademy.frontend.coupon.service.CouponService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Collections;
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
        ResponseUser user = userAdapter.getUser(userId).getBody();
        if (user == null) {
            log.warn("CouponServiceImpl: User not found for userId: {}", userId);
            return Collections.emptyList();
        }
        Long userNo = user.getUserNo();
        log.info("CouponServiceImpl: userNo received from UserAdapter: {}", userNo);
        return couponAdapter.getActiveUserCoupons(userNo).getBody();
    }
}
