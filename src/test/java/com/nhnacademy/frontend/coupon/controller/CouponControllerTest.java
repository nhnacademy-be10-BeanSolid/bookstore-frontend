package com.nhnacademy.frontend.coupon.controller;

import com.nhnacademy.frontend.auth.filter.JwtAuthenticationFilter;
import com.nhnacademy.frontend.auth.principal.CustomPrincipal;
import com.nhnacademy.frontend.common.adapter.CouponAdapter;
import com.nhnacademy.frontend.common.adapter.UserAdapter;
import com.nhnacademy.frontend.common.adapter.dto.user.response.ResponseUser;
import com.nhnacademy.frontend.coupon.dto.UserCouponResponse;
import com.nhnacademy.frontend.coupon.service.CouponService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(
        controllers = CouponController.class,
        excludeFilters = @ComponentScan.Filter(
                type = FilterType.ASSIGNABLE_TYPE,
                classes = JwtAuthenticationFilter.class),
        properties = {
                "spring.session.store-type=none",
                "spring.thymeleaf.enabled=false"
        })


@AutoConfigureMockMvc(addFilters = false)
class CouponControllerTest {

    @Autowired MockMvc mockMvc;
    @MockBean CouponService couponService;
    @MockBean RedisConnectionFactory redisConnectionFactory;
    @MockBean
    private CouponAdapter couponAdapter;
    @MockBean
    private UserAdapter userAdapter;

    private Authentication auth;

    @BeforeEach
    void setUp() {
        auth = new UsernamePasswordAuthenticationToken(
                new CustomPrincipal("1", "ROLE_USER"),
                null,
                List.of(new SimpleGrantedAuthority("ROLE_USER")));

        // userAdapter.getUser() Mocking
        when(userAdapter.getUser(anyString()))
                .thenReturn(new ResponseEntity<>(ResponseUser.builder().userNo(1L).build(), HttpStatus.OK));
    }


    @Test
    @DisplayName("인증된 사용자의 활성 쿠폰 조회 - 성공")
    void getMyActiveCoupons_authenticatedUser_success() throws Exception {
        var active = List.of(UserCouponResponse.builder()
                .userCouponId(1L).couponName("Test").couponDiscountAmount(1_000)
                .expiredAt(LocalDateTime.now().plusDays(7)).build());

        when(couponService.getActiveUserCoupons(anyLong())).thenReturn(active);

        mockMvc.perform(get("/my-coupons").principal(auth))
                .andExpect(status().isOk())
                .andExpect(view().name("coupon/my-coupons")) // 오타 수정
                .andExpect(model().attribute("activeCoupons", active));
    }

    @Test
    @DisplayName("인증되지 않은 사용자의 활성 쿠폰 조회 - 리다이렉트")
    void getMyActiveCoupons_unauthenticatedUser_redirect() throws Exception {
        mockMvc.perform(get("/my-coupons"))       // ② null principal 전달 안 함
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/auth/login"));
    }

    @Test
    @DisplayName("쿠폰 발급 - 성공")
    void issueCoupon_success() throws Exception {

        // 더미 응답 객체
        UserCouponResponse issued = UserCouponResponse.builder()
                .userCouponId(99L)
                .couponName("발급쿠폰")
                .couponDiscountAmount(1000)
                .expiredAt(LocalDateTime.now().plusDays(7))
                .build();

        when(couponService.issueCouponToUser(anyLong(), anyLong()))
                .thenReturn(issued);   // ← long 대신 DTO 반환

        mockMvc.perform(post("/my-coupons/issue")
                        .param("couponPolicyId", "1")
                        .principal(auth))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/my-coupons"))
                .andExpect(flash().attributeExists("message"));
    }
    @Test
    @DisplayName("쿠폰 발급 - 인증 안 된 경우")
    void issueCoupon_unauthenticatedUser() throws Exception {
        mockMvc.perform(post("/my-coupons/issue").param("couponPolicyId", "1"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/auth/login"))
                .andExpect(flash().attributeExists("error"));
    }

    @Test
    @DisplayName("쿠폰 발급 - 서비스 예외 처리")
    void issueCoupon_serviceError() throws Exception {
        doThrow(new RuntimeException("쿠폰 발급 실패"))
                .when(couponService).issueCouponToUser(anyLong(), anyLong());

        mockMvc.perform(post("/my-coupons/issue")
                        .param("couponPolicyId", "1")
                        .principal(auth))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/my-coupons"))
                .andExpect(flash().attributeExists("error"));
    }
}