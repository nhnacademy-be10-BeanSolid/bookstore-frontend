package com.nhnacademy.frontend.coupon.controller;

import com.nhnacademy.frontend.coupon.dto.UserCouponResponse;
import com.nhnacademy.frontend.coupon.service.CouponService;
import com.nhnacademy.frontend.auth.principal.CustomPrincipal;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Slf4j
@Controller
@RequestMapping("/my-coupons")
@RequiredArgsConstructor
public class CouponController {

    private final CouponService couponService;

    @GetMapping
    public String getMyActiveCoupons(Authentication authentication, Model model) {
        log.info("CouponController: Authentication object: {}", authentication);
        if (authentication == null || !authentication.isAuthenticated()) {
            log.info("CouponController: User not authenticated, redirecting to login.");
            // 사용자가 인증되지 않은 경우 로그인 페이지로 리다이렉트 또는 에러 처리
            return "redirect:/auth/login";
        }
        com.nhnacademy.frontend.auth.principal.CustomPrincipal customPrincipal = (com.nhnacademy.frontend.auth.principal.CustomPrincipal) authentication.getPrincipal();
        String userNo = customPrincipal.getUsername();
        log.info("Frontend CouponController: Fetching coupons for userNo (from Authentication): {}", userNo);

        List<UserCouponResponse> activeCoupons = couponService.getActiveUserCoupons(userNo);
        model.addAttribute("activeCoupons", activeCoupons);

        return "coupon/my-coupons";
    }
}
