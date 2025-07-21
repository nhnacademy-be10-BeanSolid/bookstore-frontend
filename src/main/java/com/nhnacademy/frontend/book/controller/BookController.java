package com.nhnacademy.frontend.book.controller;

import com.nhnacademy.frontend.auth.principal.CustomPrincipal;
import com.nhnacademy.frontend.common.adapter.dto.book.response.BookDetailResponseDto;
import com.nhnacademy.frontend.common.adapter.CouponAdapter;
import com.nhnacademy.frontend.common.adapter.UserAdapter;
import com.nhnacademy.frontend.common.service.BookService;

import com.nhnacademy.frontend.coupon.domain.CouponScope;
import com.nhnacademy.frontend.coupon.dto.CouponPolicyResponse;
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
import org.springframework.security.core.Authentication;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Controller
@RequiredArgsConstructor
@RequestMapping("/books")
public class BookController {

    private final BookService bookService;
    private final CouponAdapter couponAdapter;
    private final UserAdapter userAdapter;

    @GetMapping("/{bookId}")
    public String bookDetail(@PathVariable("bookId") Long bookId, Model model) {
        BookDetailResponseDto bookDetail = bookService.getBookDetail(bookId);
        model.addAttribute("book", bookDetail);

        List<CouponPolicyResponse> allCouponPolicies = couponAdapter.getAllCouponPolicies();
        log.info("Fetched all coupon policies: {}", allCouponPolicies);
        List<CouponPolicyResponse> bookCoupons = allCouponPolicies.stream()
                .filter(policy -> policy.getCouponScope() == CouponScope.BOOK && policy.getBookIds() != null && policy.getBookIds().contains(bookId))
                .collect(Collectors.toList());
        model.addAttribute("bookCoupons", bookCoupons);

        return "book/book-detail";
    }

    @PostMapping
    public String bookOrders(@RequestParam Long bookId,
                             @RequestParam String title,
                             @RequestParam Integer salePrice,
                             @RequestParam Boolean wrappable,
                             @RequestParam Integer quantity,
                             RedirectAttributes redirectAttributes) {
        redirectAttributes.addFlashAttribute("bookId", bookId);
        redirectAttributes.addFlashAttribute("title", title);
        redirectAttributes.addFlashAttribute("salePrice", salePrice);
        redirectAttributes.addFlashAttribute("wrappable", wrappable);
        redirectAttributes.addFlashAttribute("quantity", quantity);

        return "redirect:/orders";
    }

    @PostMapping("/issue-coupon")
    public String issueCouponToUser(@RequestParam Long couponPolicyId, @RequestParam Long bookId, RedirectAttributes redirectAttributes, Authentication authentication) {
        String userId = ((CustomPrincipal) authentication.getPrincipal()).getUsername();
        Long userNo = userAdapter.getUser(userId).getBody().getUserNo();
        try {
            couponAdapter.issueCouponToUser(userNo, couponPolicyId);
            redirectAttributes.addFlashAttribute("message", "쿠폰이 성공적으로 발급되었습니다!");
        } catch (Exception e) {
            log.error("Failed to issue coupon {} to user {}: {}", couponPolicyId, userNo, e.getMessage());
            redirectAttributes.addFlashAttribute("error", "쿠폰 발급 중 오류가 발생했습니다: " + e.getMessage());
        }
        return "redirect:/books/" + bookId;
    }
}
