package com.nhnacademy.frontend.coupon.controller;

import com.nhnacademy.frontend.auth.filter.JwtAuthenticationFilter;
import com.nhnacademy.frontend.auth.principal.CustomPrincipal;
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
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

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
    @DisplayName("인증된 사용자가 활성 쿠폰 페이지에 접근할 때 200 OK와 올바른 뷰, 모델 속성이 반환되는지 테스트")
    void getMyActiveCoupons_authenticatedUser_success() throws Exception {
        // Given
        CustomPrincipal principal = new CustomPrincipal("testUser", "accessToken");
        Authentication authentication = new UsernamePasswordAuthenticationToken(principal, null, Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER")));

        UserCouponResponse coupon1 = createCoupon(1L, "TestCoupon1");
        List<UserCouponResponse> activeCoupons = Collections.singletonList(coupon1);

        when(couponService.getActiveUserCoupons(anyString())).thenReturn(activeCoupons);

        // When & Then
        mockMvc.perform(get("/my-coupons").principal(authentication))
                .andExpect(status().isOk())
                .andExpect(view().name("coupon/my-coupons"))
                .andExpect(model().attributeExists("activeCoupons"))
                .andExpect(model().attribute("activeCoupons", activeCoupons));
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
