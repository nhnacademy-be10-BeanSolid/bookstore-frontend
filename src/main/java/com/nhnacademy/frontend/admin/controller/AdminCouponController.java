package com.nhnacademy.frontend.admin.controller;

import com.nhnacademy.frontend.common.adapter.BookAdapter;
import com.nhnacademy.frontend.common.adapter.CouponAdapter;
import com.nhnacademy.frontend.coupon.dto.CouponPolicyResponse;
import com.nhnacademy.frontend.coupon.dto.IssueBookCouponRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Slf4j
@Controller
@RequestMapping("/admin/coupons")
@RequiredArgsConstructor
public class AdminCouponController {

    private final CouponAdapter couponAdapter;
    private final BookAdapter bookAdapter;

    @GetMapping
    public String getAdminCouponPage(Model model, @PageableDefault(size = 100) Pageable pageable) {
        List<CouponPolicyResponse> couponPolicies = couponAdapter.getAllCouponPolicies();
        model.addAttribute("couponPolicies", couponPolicies);
        model.addAttribute("books", bookAdapter.getAllBooks(pageable).getContent());
        return "admin/coupon/coupon-management";
    }

    @PostMapping("/issue-all/{couponPolicyId}")
    public String issueCouponToAllUsers(@PathVariable Long couponPolicyId, RedirectAttributes redirectAttributes) {
        try {
            couponAdapter.startIssuingCouponsToAllUsers(couponPolicyId);
            redirectAttributes.addFlashAttribute("message", "쿠폰 발급 요청이 성공적으로 접수되었습니다.");
        } catch (Exception e) {
            log.error("Failed to issue coupon to all users for policy {}: {}", couponPolicyId, e.getMessage());
            redirectAttributes.addFlashAttribute("error", "쿠폰 발급 요청 중 오류가 발생했습니다: " + e.getMessage());
        }
        return "redirect:/admin/coupons";
    }

    @PostMapping("/issue-book")
    public String issueCouponToBook(@RequestParam Long couponPolicyId, @RequestParam Long bookId, RedirectAttributes redirectAttributes) {
        try {
            couponAdapter.issueCouponToBook(couponPolicyId, bookId);
            redirectAttributes.addFlashAttribute("message", "도서 쿠폰 발급 요청이 성공적으로 접수되었습니다.");
        } catch (Exception e) {
            log.error("Failed to issue coupon to book for policy {} and book {}: {}", couponPolicyId, bookId, e.getMessage());
            redirectAttributes.addFlashAttribute("error", "도서 쿠폰 발급 요청 중 오류가 발생했습니다: " + e.getMessage());
        }
        return "redirect:/admin/coupons";
    }

    @PostMapping("/issue-book-to-user")
    public String issueBookCouponToUser(IssueBookCouponRequest request, RedirectAttributes redirectAttributes) {
        try {
            couponAdapter.issueBookCoupon(request);
            redirectAttributes.addFlashAttribute("message", "사용자에게 도서 쿠폰 발급 요청이 성공적으로 접수되었습니다.");
        } catch (Exception e) {
            log.error("Failed to issue book coupon to user {}: {}", request.getUserId(), e.getMessage());
            redirectAttributes.addFlashAttribute("error", "사용자에게 도서 쿠폰 발급 요청 중 오류가 발생했습니다: " + e.getMessage());
        }
        return "redirect:/admin/coupons";
    }
}
