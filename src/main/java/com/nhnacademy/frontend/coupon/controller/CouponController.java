package com.nhnacademy.frontend.coupon.controller;

import com.nhnacademy.frontend.auth.principal.CustomPrincipal;
import com.nhnacademy.frontend.common.adapter.UserAdapter;
import com.nhnacademy.frontend.common.adapter.dto.user.response.ResponseUser;
import com.nhnacademy.frontend.coupon.dto.IssueCategoryCouponRequest;
import com.nhnacademy.frontend.coupon.dto.UserCouponResponse;
import com.nhnacademy.frontend.coupon.service.CouponService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
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
    private final UserAdapter userAdapter;

    @GetMapping
    public String getMyActiveCoupons(Authentication authentication, Model model) {
        if (!isAuthenticated(authentication)) {
            return REDIRECT_LOGIN;
        }

        CustomPrincipal principal = (CustomPrincipal) authentication.getPrincipal();
        log.info("getMyActiveCoupons: username={}, userType={}", principal.getUsername(), principal.getUserType());

        if (isAdmin(principal)) {
            model.addAttribute(ACTIVE_COUPONS, Collections.emptyList());
            return VIEW_MY_COUPON;
        }

        Long userNo = fetchUserNo(principal);
        if (userNo == null) {
            log.error("getMyActiveCoupons: userNo is null for username={}", principal.getUsername());
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

        if (!isAuthenticated(authentication)) {
            redirect.addFlashAttribute(FLASH_ATTR_ERROR, "로그인이 필요합니다.");
            return REDIRECT_LOGIN;
        }

        CustomPrincipal principal = (CustomPrincipal) authentication.getPrincipal();
        log.info("issueCoupon: username={}, userType={}", principal.getUsername(), principal.getUserType());

        if (isAdmin(principal)) {
            redirect.addFlashAttribute(FLASH_ATTR_ERROR, "관리자는 쿠폰을 발급받을 수 없습니다.");
            return REDIRECT_MY_COUPONS;
        }

        Long userNo = fetchUserNo(principal);
        if (userNo == null) {
            redirect.addFlashAttribute(FLASH_ATTR_ERROR, "쿠폰 발급에 실패했습니다. (오류: 사용자 ID를 가져오지 못했습니다.)");
            return REDIRECT_MY_COUPONS;
        }

        try {
            couponService.issueCouponToUser(userNo, couponPolicyId);
            redirect.addFlashAttribute(FLASH_ATTR_MESSAGE, "쿠폰이 성공적으로 발급되었습니다!");
        } catch (Exception e) {
            log.error("issueCoupon: 쿠폰 발급 오류", e);
            redirect.addFlashAttribute(FLASH_ATTR_ERROR, "쿠폰 발급 중 문제가 발생했습니다.");
        }

        return REDIRECT_MY_COUPONS;
    }

    @PostMapping("/issue/category")
    public String issueCategoryCoupon(@RequestBody IssueCategoryCouponRequest request,
                                      Authentication authentication,
                                      RedirectAttributes redirect) {

        if (!isAuthenticated(authentication)) {
            redirect.addFlashAttribute(FLASH_ATTR_ERROR, "로그인이 필요합니다.");
            return REDIRECT_LOGIN;
        }

        CustomPrincipal principal = (CustomPrincipal) authentication.getPrincipal();
        log.info("issueCategoryCoupon: username={}, userType={}", principal.getUsername(), principal.getUserType());

        if (isAdmin(principal)) {
            redirect.addFlashAttribute(FLASH_ATTR_ERROR, "관리자는 쿠폰을 발급받을 수 없습니다.");
            return REDIRECT_MY_COUPONS;
        }

        Long userNo = fetchUserNo(principal);
        if (userNo == null) {
            redirect.addFlashAttribute(FLASH_ATTR_ERROR, "쿠폰 발급에 실패했습니다. (오류: 사용자 ID를 가져오지 못했습니다.)");
            return REDIRECT_MY_COUPONS;
        }

        try {
            couponService.issueCategoryCoupon(userNo, request.getCouponPolicyId(), request.getCategoryId());
            redirect.addFlashAttribute(FLASH_ATTR_MESSAGE, "카테고리 쿠폰이 성공적으로 발급되었습니다!");
        } catch (Exception e) {
            log.error("issueCategoryCoupon: 카테고리 쿠폰 발급 오류", e);
            redirect.addFlashAttribute(FLASH_ATTR_ERROR, "카테고리 쿠폰 발급 중 문제가 발생했습니다.");
        }

        return REDIRECT_MY_COUPONS;
    }

    private boolean isAuthenticated(Authentication authentication) {
        return authentication != null && authentication.isAuthenticated();
    }

    private boolean isAdmin(CustomPrincipal principal) {
        return "ADMIN".equalsIgnoreCase(principal.getUserType());
    }

    private Long fetchUserNo(CustomPrincipal principal) {
        try {
            ResponseEntity<ResponseUser> response = userAdapter.getUser(principal.getUsername());
            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                return response.getBody().getUserNo();
            } else {
                log.error("fetchUserNo: Failed to get user. status={}, username={}", response.getStatusCode(), principal.getUsername());
            }
        } catch (Exception e) {
            log.error("fetchUserNo: Exception for username={}. msg={}", principal.getUsername(), e.getMessage(), e);
        }
        return null;
    }
}