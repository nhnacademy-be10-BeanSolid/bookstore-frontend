package com.nhnacademy.frontend.coupon.controller;

import com.nhnacademy.frontend.auth.principal.CustomPrincipal;
import com.nhnacademy.frontend.coupon.dto.UserCouponResponse;
import com.nhnacademy.frontend.coupon.service.CouponService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Collections;
import java.util.List;

@Slf4j
@Controller
@RequestMapping("/my-coupons")
@RequiredArgsConstructor
public class CouponController {

    private static final String ACTIVE_COUPONS = "activeCoupons";   // 중복 리터럴 상수화
    private final CouponService couponService;
    @GetMapping
    public String getMyActiveCoupons(Authentication authentication, Model model) {

        if (authentication == null || !authentication.isAuthenticated()) {
            return "redirect:/auth/login";
        }

        CustomPrincipal principal = (CustomPrincipal) authentication.getPrincipal();

        // 관리자라면 빈 리스트 반환
        if ("ADMIN".equals(principal.getUserType())) {
            model.addAttribute(ACTIVE_COUPONS, Collections.emptyList());
            return "coupon/my-coupon";
        }

        try {
            Long userNo = Long.parseLong(principal.getUsername());
            List<UserCouponResponse> coupons = couponService.getActiveUserCoupons(userNo);
            model.addAttribute(ACTIVE_COUPONS, coupons);
        } catch (NumberFormatException e) {
            log.error("userNo 파싱 실패: {}", e.getMessage());
            model.addAttribute(ACTIVE_COUPONS, Collections.emptyList());
        }

        return "coupon/my-coupon";
    }

    @PostMapping("/issue")
    public String issueCoupon(@RequestParam Long couponPolicyId,
                              Authentication authentication,
                              RedirectAttributes redirect) {

        if (authentication == null || !authentication.isAuthenticated()) {
            redirect.addFlashAttribute("error", "로그인이 필요합니다.");
            return "redirect:/auth/login";
        }

        CustomPrincipal principal = (CustomPrincipal) authentication.getPrincipal();

        if ("ADMIN".equals(principal.getUserType())) {
            redirect.addFlashAttribute("error", "관리자는 쿠폰을 발급받을 수 없습니다.");
            return "redirect:/my-coupons";
        }

        try {
            Long userNo = Long.parseLong(principal.getUsername());
            couponService.issueCouponToUser(userNo, couponPolicyId);
            redirect.addFlashAttribute("message", "쿠폰이 성공적으로 발급되었습니다!");
        } catch (NumberFormatException e) {
            log.error("userNo 파싱 실패: {}", e.getMessage());
            redirect.addFlashAttribute("error", "사용자 정보를 가져오는 데 실패했습니다.");
        } catch (Exception e) {
            log.error("쿠폰 발급 오류: {}", e.getMessage());
            redirect.addFlashAttribute("error", "쿠폰 발급 중 문제가 발생했습니다.");
        }

        return "redirect:/my-coupons";
    }
}