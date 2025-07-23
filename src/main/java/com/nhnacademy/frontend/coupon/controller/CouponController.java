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
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

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
        CustomPrincipal customPrincipal = (CustomPrincipal) authentication.getPrincipal();
        if ("ADMIN".equals(customPrincipal.getUserType())) {
            log.info("CouponController: Admin user, returning empty coupon list.");
            model.addAttribute("activeCoupons", java.util.Collections.emptyList());
            return "coupon/my-coupon";
        }

        Long userNo = null;
        try {
            userNo = Long.parseLong(customPrincipal.getUsername());
            log.info("Frontend CouponController: Fetching coupons for userNo (from Authentication): {}", userNo);
            List<UserCouponResponse> activeCoupons = couponService.getActiveUserCoupons(userNo);
            model.addAttribute("activeCoupons", activeCoupons);
        } catch (NumberFormatException e) {
            log.error("CouponController: Failed to parse userNo from username '{}'. Returning empty coupon list. Error: {}", customPrincipal.getUsername(), e.getMessage());
            model.addAttribute("activeCoupons", java.util.Collections.emptyList());
        }

        return "coupon/my-coupon";
    }

    @PostMapping("/issue")
    public String issueCoupon(@RequestParam Long couponPolicyId, Authentication authentication, RedirectAttributes redirectAttributes) {
        if (authentication == null || !authentication.isAuthenticated()) {
            redirectAttributes.addFlashAttribute("error", "로그인이 필요합니다.");
            return "redirect:/auth/login";
        }
        CustomPrincipal customPrincipal = (CustomPrincipal) authentication.getPrincipal();
        if ("ADMIN".equals(customPrincipal.getUserType())) {
            redirectAttributes.addFlashAttribute("error", "관리자 계정은 쿠폰을 발급받을 수 없습니다.");
            return "redirect:/my-coupons";
        }
        Long userNo = null;
        try {
            userNo = Long.parseLong(customPrincipal.getUsername());
        } catch (NumberFormatException e) {
            log.error("CouponController: Failed to parse userNo from username '{}'. Coupon issuance failed. Error: {}", customPrincipal.getUsername(), e.getMessage());
            redirectAttributes.addFlashAttribute("error", "사용자 정보를 가져오는 데 실패했습니다. 쿠폰 발급에 실패했습니다.");
            return "redirect:/my-coupons";
        }

        try {
            couponService.issueCouponToUser(userNo, couponPolicyId);
            redirectAttributes.addFlashAttribute("message", "쿠폰이 성공적으로 발급되었습니다!");
        } catch (Exception e) {
            log.error("쿠폰 발급 중 오류 발생: {}", e.getMessage());
            redirectAttributes.addFlashAttribute("error", "쿠폰 발급 중 오류가 발생했습니다: " + e.getMessage());
        }
        return "redirect:/my-coupons";
    }
}