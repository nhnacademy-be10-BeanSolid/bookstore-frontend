package com.nhnacademy.frontend.coupon.controller;

import com.nhnacademy.frontend.auth.principal.CustomPrincipal;
import com.nhnacademy.frontend.coupon.dto.UserCouponResponse;
import com.nhnacademy.frontend.coupon.service.CouponService;
import com.nhnacademy.frontend.user.adapter.UserAdapter; // 추가
import com.nhnacademy.frontend.user.dto.response.UserResponse; // 추가
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

    private static final String ACTIVE_COUPONS      = "activeCoupons";
    private static final String FLASH_ATTR_ERROR    = "error";
    private static final String FLASH_ATTR_MESSAGE  = "message";
    private static final String VIEW_MY_COUPON      = "coupon/my-coupons";
    private static final String REDIRECT_MY_COUPONS = "redirect:/my-coupons";
    private static final String REDIRECT_LOGIN      = "redirect:/auth/login";

    private final CouponService couponService;
    private final UserAdapter userAdapter; // 추가

    @GetMapping
    public String getMyActiveCoupons(Authentication authentication, Model model) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return REDIRECT_LOGIN;
        }

        CustomPrincipal principal = (CustomPrincipal) authentication.getPrincipal();
        log.info("getMyActiveCoupons: principal.getUsername() = {}, principal.getUserType() = {}", principal.getUsername(), principal.getUserType());


        if ("admin".equals(principal.getUsername()) && "ADMIN".equals(principal.getUserType())) {
            model.addAttribute(ACTIVE_COUPONS, Collections.emptyList());
            return VIEW_MY_COUPON;
        }

        if ("ADMIN".equals(principal.getUserType())) {
            model.addAttribute(ACTIVE_COUPONS, Collections.emptyList());
            return VIEW_MY_COUPON;
        }

        // principal.getUsername() (로그인 ID)를 사용하여 userNo 조회
        Long userNo = null;
        try {
            UserResponse userResponse = userAdapter.getUserByUserId(principal.getUsername());
            userNo = userResponse.getUserNo();
        } catch (Exception e) {
            log.error("CouponController: Failed to get userNo for username '{}'. Error: {}", principal.getUsername(), e.getMessage());
            model.addAttribute("errorMessage", "사용자 정보를 가져오는 데 실패했습니다. 쿠폰 목록을 불러올 수 없습니다.");
            model.addAttribute(ACTIVE_COUPONS, Collections.emptyList());
            return VIEW_MY_COUPON;
        }

        if (userNo == null) {
            log.error("CouponController: userNo is null for username '{}'.", principal.getUsername());
            model.addAttribute("errorMessage", "사용자 정보를 가져오는 데 실패했습니다. 쿠폰 목록을 불러올 수 없습니다.");
            model.addAttribute(ACTIVE_COUPONS, Collections.emptyList());
            return VIEW_MY_COUPON;
        }

        List<UserCouponResponse> coupons = couponService.getActiveUserCoupons(userNo);
        model.addAttribute(ACTIVE_COUPONS, coupons);

        return VIEW_MY_COUPON;
    }


    @PostMapping("/issue")
    public String issueCoupon(@RequestParam Long couponPolicyId,
                              Authentication authentication,
                              RedirectAttributes redirect) {

        if (authentication == null || !authentication.isAuthenticated()) {
            redirect.addFlashAttribute(FLASH_ATTR_ERROR, "로그인이 필요합니다.");
            return REDIRECT_LOGIN;
        }

        CustomPrincipal principal = (CustomPrincipal) authentication.getPrincipal();
        log.info("issueCoupon: principal.getUsername() = {}, principal.getUserType() = {}", principal.getUsername(), principal.getUserType());

        if ("ADMIN".equals(principal.getUserType())) {
            redirect.addFlashAttribute(FLASH_ATTR_ERROR, "관리자는 쿠폰을 발급받을 수 없습니다.");
            return REDIRECT_MY_COUPONS;
        }

        // principal.getUsername() (로그인 ID)를 사용하여 userNo 조회
        Long userNo = null;
        try {
            UserResponse userResponse = userAdapter.getUserByUserId(principal.getUsername());
            userNo = userResponse.getUserNo();
        } catch (Exception e) {
            log.error("CouponController: Failed to get userNo for username '{}' during coupon issue. Error: {}", principal.getUsername(), e.getMessage());
            redirect.addFlashAttribute(FLASH_ATTR_ERROR, "쿠폰 발급에 실패했습니다. (오류: 사용자 ID를 가져오지 못했습니다.)");
            return REDIRECT_MY_COUPONS;
        }

        if (userNo == null) {
            log.error("CouponController: userNo is null for username '{}' during coupon issue.", principal.getUsername());
            redirect.addFlashAttribute(FLASH_ATTR_ERROR, "쿠폰 발급에 실패했습니다. (오류: 사용자 ID를 가져오지 못했습니다.)");
            return REDIRECT_MY_COUPONS;
        }

        try {
            couponService.issueCouponToUser(userNo, couponPolicyId);
            redirect.addFlashAttribute(FLASH_ATTR_MESSAGE, "쿠폰이 성공적으로 발급되었습니다!");
        } catch (Exception e) {
            log.error("쿠폰 발급 오류: {}", e.getMessage(), e);
            redirect.addFlashAttribute(FLASH_ATTR_ERROR, "쿠폰 발급 중 문제가 발생했습니다.");
        }

        return REDIRECT_MY_COUPONS;
    }

}
