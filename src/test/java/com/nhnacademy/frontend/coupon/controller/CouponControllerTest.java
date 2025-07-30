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
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
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
        },
        excludeAutoConfiguration = {
                org.springframework.boot.autoconfigure.session.SessionAutoConfiguration.class,
                org.springframework.cloud.openfeign.FeignAutoConfiguration.class
        })


@AutoConfigureMockMvc(addFilters = false)
class CouponControllerTest {

    @Autowired
    MockMvc mockMvc;
    @MockBean
    CouponService couponService;
    @MockBean
    RedisConnectionFactory redisConnectionFactory;
    @MockBean
    private CouponAdapter couponAdapter;
    @MockBean
    private UserAdapter userAdapter;

    private Authentication userAuth;
    private Authentication adminAuth;

    @BeforeEach
    void setUp() {
        userAuth = new UsernamePasswordAuthenticationToken(
                new CustomPrincipal("1", "ROLE_USER"),
                null,
                List.of(new SimpleGrantedAuthority("ROLE_USER")));

        adminAuth = new UsernamePasswordAuthenticationToken(
                new CustomPrincipal("2", "ADMIN"),
                null,
                List.of(new SimpleGrantedAuthority("ROLE_ADMIN")));

        // userAdapter.getUser() Mocking for userAuth
        when(userAdapter.getUser("1"))
                .thenReturn(new ResponseEntity<>(ResponseUser.builder().userNo(1L).build(), HttpStatus.OK));

        // userAdapter.getUser() Mocking for adminAuth
        when(userAdapter.getUser("2"))
                .thenReturn(new ResponseEntity<>(ResponseUser.builder().userNo(2L).build(), HttpStatus.OK));
    }


    @Test
    @DisplayName("인증된 사용자의 활성 쿠폰 조회 - 성공")
    void getMyActiveCoupons_authenticatedUser_success() throws Exception {
        var active = List.of(UserCouponResponse.builder()
                .userCouponId(1L).couponName("Test").couponDiscountAmount(1_000)
                .expiredAt(LocalDateTime.now().plusDays(7)).build());

        when(couponService.getActiveUserCoupons(anyLong())).thenReturn(active);

        mockMvc.perform(get("/my-coupons").principal(userAuth))
                .andExpect(status().isOk())
                .andExpect(view().name("coupon/my-coupons"))
                .andExpect(model().attribute("activeCoupons", active));
    }

    @Test
    @DisplayName("인증되지 않은 사용자의 활성 쿠폰 조회 - 리다이렉트")
    void getMyActiveCoupons_unauthenticatedUser_redirect() throws Exception {
        mockMvc.perform(get("/my-coupons"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/auth/login"));
    }

    @Test
    @DisplayName("인증된 관리자 사용자의 활성 쿠폰 조회 - 성공 (빈 목록)")
    void getMyActiveCoupons_adminUser_success() throws Exception {
        mockMvc.perform(get("/my-coupons").principal(adminAuth))
                .andExpect(status().isOk())
                .andExpect(view().name("coupon/my-coupons"))
                .andExpect(model().attribute("activeCoupons", Collections.emptyList()));
        verify(couponService, never()).getActiveUserCoupons(anyLong());
    }

    @Test
    @DisplayName("활성 쿠폰 조회 - 사용자 번호 null 시 에러 메시지")
    void getMyActiveCoupons_userNoNull_errorMessage() throws Exception {
        when(userAdapter.getUser("1"))
                .thenReturn(new ResponseEntity<>(null, HttpStatus.OK)); // userNo가 null인 경우

        mockMvc.perform(get("/my-coupons").principal(userAuth))
                .andExpect(status().isOk())
                .andExpect(view().name("coupon/my-coupons"))
                .andExpect(model().attributeExists("errorMessage"))
                .andExpect(model().attribute("errorMessage", "사용자 정보를 가져오는 데 실패했습니다. 쿠폰 목록을 불러올 수 없습니다."));
        verify(couponService, never()).getActiveUserCoupons(anyLong());
    }

    @Test
    @DisplayName("활성 쿠폰 조회 - 서비스 예외 처리 시 에러 메시지")
    void getMyActiveCoupons_serviceError_errorMessage() throws Exception {
        when(userAdapter.getUser("1"))
                .thenReturn(new ResponseEntity<>(ResponseUser.builder().userNo(1L).build(), HttpStatus.OK));
        doThrow(new RuntimeException("쿠폰 조회 실패"))
                .when(couponService).getActiveUserCoupons(anyLong());

        mockMvc.perform(get("/my-coupons").principal(userAuth))
                .andExpect(status().isOk())
                .andExpect(view().name("error/error"))
                .andExpect(model().attributeExists("userFriendlyMessage"))
                .andExpect(model().attribute("userFriendlyMessage", "서버 내부 오류가 발생했습니다."));
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
                .thenReturn(issued);

        mockMvc.perform(post("/my-coupons/issue")
                        .param("couponPolicyId", "1")
                        .principal(userAuth))
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
                        .principal(userAuth))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/my-coupons"))
                .andExpect(flash().attributeExists("error"));
    }

    @Test
    @DisplayName("쿠폰 발급 - 관리자 계정 리다이렉트")
    void issueCoupon_adminUser_redirect() throws Exception {
        mockMvc.perform(post("/my-coupons/issue")
                        .param("couponPolicyId", "1")
                        .principal(adminAuth))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/my-coupons"))
                .andExpect(flash().attributeExists("error"));
        verify(couponService, never()).issueCouponToUser(anyLong(), anyLong());
    }

    @Test
    @DisplayName("쿠폰 발급 - 사용자 번호 null 시 에러 메시지")
    void issueCoupon_userNoNull_errorMessage() throws Exception {
        when(userAdapter.getUser("1"))
                .thenReturn(new ResponseEntity<>(null, HttpStatus.OK)); // userNo가 null인 경우

        mockMvc.perform(post("/my-coupons/issue")
                        .param("couponPolicyId", "1")
                        .principal(userAuth))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/my-coupons"))
                .andExpect(flash().attributeExists("error"));
        verify(couponService, never()).issueCouponToUser(anyLong(), anyLong());
    }

    @Test
    @DisplayName("카테고리 쿠폰 발급 - 성공")
    void issueCategoryCoupon_authenticatedUser_success() throws Exception {
        when(couponService.issueCategoryCoupon(anyLong(), anyLong(), anyLong()))
                .thenReturn(UserCouponResponse.builder().userCouponId(1L).build());

        mockMvc.perform(post("/my-coupons/issue/category")
                        .contentType("application/json")
                        .content("{\"couponPolicyId\":1, \"categoryId\":10}")
                        .principal(userAuth))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/my-coupons"))
                .andExpect(flash().attributeExists("message"));
    }

    @Test
    @DisplayName("카테고리 쿠폰 발급 - 인증 안 된 경우")
    void issueCategoryCoupon_unauthenticatedUser_redirect() throws Exception {
        mockMvc.perform(post("/my-coupons/issue/category")
                        .contentType("application/json")
                        .content("{\"couponPolicyId\":1, \"categoryId\":10}"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/auth/login"))
                .andExpect(flash().attributeExists("error"));
    }

    @Test
    @DisplayName("카테고리 쿠폰 발급 - 서비스 예외 처리")
    void issueCategoryCoupon_serviceError() throws Exception {
        doThrow(new RuntimeException("카테고리 쿠폰 발급 실패"))
                .when(couponService).issueCategoryCoupon(anyLong(), anyLong(), anyLong());

        mockMvc.perform(post("/my-coupons/issue/category")
                        .contentType("application/json")
                        .content("{\"couponPolicyId\":1, \"categoryId\":10}")
                        .principal(userAuth))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/my-coupons"))
                .andExpect(flash().attributeExists("error"));
    }

    @Test
    @DisplayName("카테고리 쿠폰 발급 - 관리자 계정 리다이렉트")
    void issueCategoryCoupon_adminUser_redirect() throws Exception {
        mockMvc.perform(post("/my-coupons/issue/category")
                        .contentType("application/json")
                        .content("{\"couponPolicyId\":1, \"categoryId\":10}")
                        .principal(adminAuth))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/my-coupons"))
                .andExpect(flash().attributeExists("error"));
        verify(couponService, never()).issueCategoryCoupon(anyLong(), anyLong(), anyLong());
    }

    @Test
    @DisplayName("카테고리 쿠폰 발급 - 사용자 번호 null 시 에러 메시지")
    void issueCategoryCoupon_userNoNull_errorMessage() throws Exception {
        when(userAdapter.getUser("1"))
                .thenReturn(new ResponseEntity<>(null, HttpStatus.OK)); // userNo가 null인 경우

        mockMvc.perform(post("/my-coupons/issue/category")
                        .contentType("application/json")
                        .content("{\"couponPolicyId\":1, \"categoryId\":10}")
                        .principal(userAuth))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/my-coupons"))
                .andExpect(flash().attributeExists("error"));
        verify(couponService, never()).issueCategoryCoupon(anyLong(), anyLong(), anyLong());
    }

    @Test
    @DisplayName("isAuthenticated - 인증 객체가 null인 경우 false 반환")
    void isAuthenticated_nullAuthentication_returnsFalse() throws Exception {
        mockMvc.perform(get("/my-coupons"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/auth/login"));
    }

    

    @Test
    @DisplayName("isAdmin - principal이 null인 경우 false 반환")
    void isAdmin_nullPrincipal_returnsFalse() throws Exception {
        Authentication authWithNullPrincipal = new UsernamePasswordAuthenticationToken(
                null,
                null,
                Collections.emptyList());

        mockMvc.perform(get("/my-coupons").principal(authWithNullPrincipal))
                .andExpect(status().isOk())
                .andExpect(view().name("error/error"))
                .andExpect(model().attributeExists("statusCode"))
                .andExpect(model().attribute("statusCode", 500))
                .andExpect(model().attributeExists("userFriendlyMessage"))
                .andExpect(model().attribute("userFriendlyMessage", "서버 내부 오류가 발생했습니다."));
    }

    @Test
    @DisplayName("isAdmin - userType이 null인 경우 false 반환")
    void isAdmin_nullUserType_returnsFalse() throws Exception {
        Authentication authWithNullUserType = new UsernamePasswordAuthenticationToken(
                new CustomPrincipal("4", null),
                null,
                List.of(new SimpleGrantedAuthority("ROLE_USER")));

        when(userAdapter.getUser("4"))
                .thenReturn(new ResponseEntity<>(ResponseUser.builder().userNo(4L).build(), HttpStatus.OK));

        mockMvc.perform(get("/my-coupons").principal(authWithNullUserType))
                .andExpect(status().isOk())
                .andExpect(view().name("coupon/my-coupons"));
    }

    @Test
    @DisplayName("isAdmin - userType이 ADMIN이 아닌 경우 false 반환")
    void isAdmin_nonAdminUserType_returnsFalse() throws Exception {
        Authentication authWithNonAdminUserType = new UsernamePasswordAuthenticationToken(
                new CustomPrincipal("5", "ROLE_MEMBER"),
                null,
                List.of(new SimpleGrantedAuthority("ROLE_MEMBER")));

        when(userAdapter.getUser("5"))
                .thenReturn(new ResponseEntity<>(ResponseUser.builder().userNo(5L).build(), HttpStatus.OK));

        mockMvc.perform(get("/my-coupons").principal(authWithNonAdminUserType))
                .andExpect(status().isOk())
                .andExpect(view().name("coupon/my-coupons"));
    }

    @Test
    @DisplayName("fetchUserNo - userAdapter 호출 시 2xx 상태 코드가 아닌 경우 null 반환")
    void fetchUserNo_non2xxStatusCode_returnsNull() throws Exception {
        when(userAdapter.getUser(anyString()))
                .thenReturn(new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR));

        mockMvc.perform(get("/my-coupons").principal(userAuth))
                .andExpect(status().isOk())
                .andExpect(view().name("coupon/my-coupons"))
                .andExpect(model().attributeExists("errorMessage"));
    }

    @Test
    @DisplayName("fetchUserNo - userAdapter 호출 시 응답 본문이 null인 경우 null 반환")
    void fetchUserNo_nullResponseBody_returnsNull() throws Exception {
        when(userAdapter.getUser(anyString()))
                .thenReturn(new ResponseEntity<>(HttpStatus.OK));

        mockMvc.perform(get("/my-coupons").principal(userAuth))
                .andExpect(status().isOk())
                .andExpect(view().name("coupon/my-coupons"))
                .andExpect(model().attributeExists("errorMessage"));
    }

    @Test
    @DisplayName("fetchUserNo - userAdapter 호출 시 예외 발생 시 null 반환")
    void fetchUserNo_exception_returnsNull() throws Exception {
        doThrow(new RuntimeException("UserAdapter Exception"))
                .when(userAdapter).getUser(anyString());

        mockMvc.perform(get("/my-coupons").principal(userAuth))
                .andExpect(status().isOk())
                .andExpect(view().name("coupon/my-coupons"))
                .andExpect(model().attributeExists("errorMessage"));
    }
}