package com.nhnacademy.frontend.coupon.controller;

import com.nhnacademy.frontend.auth.filter.JwtAuthenticationFilter;
import com.nhnacademy.frontend.coupon.dto.UserCouponResponse;
import com.nhnacademy.frontend.coupon.service.CouponService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(
        controllers = CouponController.class,
        excludeFilters = @ComponentScan.Filter(
                type = FilterType.ASSIGNABLE_TYPE,
                classes = JwtAuthenticationFilter.class
        )
)
@AutoConfigureMockMvc(addFilters = false)
class CouponControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CouponService couponService;
    @MockBean
    private org.springframework.data.redis.connection.RedisConnectionFactory redisConnectionFactory;

    private UserCouponResponse createCoupon(Long userCouponId, String couponName) {
        return UserCouponResponse.builder()
                .userCouponId(userCouponId)
                .userNo(1L)
                .couponPolicyId(userCouponId)
                .couponName(couponName)
                .couponDiscountAmount(1000)
                .issuedAt(LocalDateTime.now())
                .expiredAt(LocalDateTime.now().plusDays(7))
                .usedAt(null)
                .status(com.nhnacademy.frontend.coupon.domain.UserCouponStatus.ACTIVE)
                .orderId(null)
                .build();
    }



    @Test
    @DisplayName("인증되지 않은 사용자가 활성 쿠폰 페이지에 접근할 때 로그인 페이지로 리다이렉트되는지 테스트")
    void getMyActiveCoupons_unauthenticatedUser_redirectsToLogin() throws Exception {
        // When & Then
        mockMvc.perform(get("/my-coupons"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/auth/login")); 
    }

}
