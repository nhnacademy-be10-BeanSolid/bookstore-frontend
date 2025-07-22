package com.nhnacademy.frontend.admin.controller;

import com.nhnacademy.frontend.admin.adapter.BookAdminAdaptor;
import com.nhnacademy.frontend.admin.adapter.CategoryAdminAdaptor;
import com.nhnacademy.frontend.admin.adapter.CouponAdminAdaptor;
import com.nhnacademy.frontend.admin.dto.request.CouponPolicyCreateRequest;
import com.nhnacademy.frontend.admin.dto.response.BookCategoryResponse;
import com.nhnacademy.frontend.admin.dto.response.BookResponse;
import com.nhnacademy.frontend.admin.dto.response.CouponPolicyResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/admin/coupons")
@RequiredArgsConstructor
public class AdminCouponController {

    private final CouponAdminAdaptor couponAdminAdaptor;
    private final BookAdminAdaptor bookAdminAdaptor;
    private final CategoryAdminAdaptor categoryAdminAdaptor;

    @GetMapping
    public String showCouponList(Model model) {
        List<CouponPolicyResponse> couponPolicies = couponAdminAdaptor.getAllCouponPolicies();
        model.addAttribute("couponPolicies", couponPolicies);
        return "admin/coupon/coupon_list";
    }

    @GetMapping("/create")
    public String showCreateCouponForm(Model model) {
        model.addAttribute("couponPolicyCreateRequest", new CouponPolicyCreateRequest());
        List<BookResponse> allBooks = bookAdminAdaptor.getAllBooks(0, Integer.MAX_VALUE, List.of()).getContent();
        model.addAttribute("allBooks", allBooks);

        List<BookCategoryResponse> allCategories = categoryAdminAdaptor.getAllCategories();
        model.addAttribute("allCategories", allCategories);

        return "admin/coupon/create_coupon_form";
    }

    @PostMapping("/create")
    public String createCouponPolicy(@ModelAttribute CouponPolicyCreateRequest request) {
        couponAdminAdaptor.createCouponPolicy(request);
        return "redirect:/admin/coupons?success=true";
    }

    @GetMapping("/{couponId}")
    public String showCouponDetail(@PathVariable Long couponId, Model model) {
        CouponPolicyResponse couponPolicy = couponAdminAdaptor.getCouponPolicyById(couponId);
        model.addAttribute("couponPolicy", couponPolicy);
        return "admin/coupon/coupon_detail";
    }

    @PostMapping("/{couponId}/delete")
    public String deleteCouponPolicy(@PathVariable Long couponId) {
        couponAdminAdaptor.deleteCouponPolicy(couponId);
        return "redirect:/admin/coupons?deleted=true";
    }
}