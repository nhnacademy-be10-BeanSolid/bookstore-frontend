package com.nhnacademy.frontend.mypage.controller;


import com.nhnacademy.frontend.auth.util.JwtCookieUtil;
import com.nhnacademy.frontend.common.adapter.dto.book.response.BookLikeResponse;
import com.nhnacademy.frontend.common.adapter.dto.user.request.AddressCreateRequest;
import com.nhnacademy.frontend.common.adapter.dto.user.request.UserUpdateRequestDto;
import com.nhnacademy.frontend.common.adapter.dto.user.response.ResponsePoint;
import com.nhnacademy.frontend.common.adapter.dto.user.response.ResponsePointType;
import com.nhnacademy.frontend.common.adapter.dto.user.response.ResponseUser;
import com.nhnacademy.frontend.mypage.dto.request.ReturnFormRequest;
import com.nhnacademy.frontend.mypage.service.MypageService;
import com.nhnacademy.frontend.order.dto.response.OrderDetailResponse;
import com.nhnacademy.frontend.order.dto.response.OrderSummaryResponse;
import feign.FeignException;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Objects;

@Slf4j
@Controller
@RequestMapping("/mypage")
@RequiredArgsConstructor
public class MypageController {
    private static final String USER_TYPE_ATTRIBUTE = "userType";
    private static final String USER_TYPE_LOCAL = "LOCAL";
    private static final String MESSAGE_ATTRIBUTE = "message";
    private static final String ERROR_ATTRIBUTE = "error";
    private static final String ERRORMESSAGE_ATTRIBUTE = "errorMessage";
    private static final String MYPAGE_VERIFIED_ATTRIBUTE = "mypage_verified";
    private final MypageService mypageService;
    private final JwtCookieUtil jwtCookieUtil;


    @GetMapping
    public String mypageForm() {
        return "mypage/form";
    }

    @PostMapping("/withdraw")
    public String mypageWithdraw(@RequestParam(value = "password", required = false) String password,
                                 HttpServletResponse response,
                                 Model model) {
        String userType = (String) model.getAttribute(USER_TYPE_ATTRIBUTE);

        boolean result;

        if("OAUTH2".equals(userType)) {
            result = mypageService.withdrawOAuth2User();
        } else if (USER_TYPE_LOCAL.equals(userType)) {
            if(password == null || password.isEmpty()) {
                return "redirect:/mypage?error=password_required";
            }
            result = mypageService.withdrawUser(password);
        } else {
            return "redirect:/mypage?error=invalid_user_type";
        }

        if(result) {
            jwtCookieUtil.removeJwtCookie(response);
            return "redirect:/";
        } else {
            return "redirect:/mypage";
        }
    }

    @GetMapping("/edit")
    public String mypageEditForm(Model model) {
        ResponseUser user = mypageService.getMyInfo();
        model.addAttribute("user", user);
        return "mypage/edit";
    }

    @PostMapping("/edit")
    public String editMyPage(@RequestParam(required = false) String password,
                             @RequestParam(required = false) String userPassword,
                             @RequestParam(required = false) String userPasswordConfirm,
                             @ModelAttribute UserUpdateRequestDto request,
                             RedirectAttributes redirectAttributes,
                             @RequestHeader(value = "Referer", required = false) String referer,
                             Model model) {

        // 비밀번호 수정 시 발생
        if ((userPassword != null || userPasswordConfirm != null) && !Objects.equals(userPassword, userPasswordConfirm)) {
            redirectAttributes.addFlashAttribute(ERROR_ATTRIBUTE, "수정할 비밀번호와 비밀번호 확인이 일치하지 않습니다.");
            return "redirect:" + (referer != null ? referer : "/mypage/edit");
        }

        String userType = (String) model.getAttribute(USER_TYPE_ATTRIBUTE);

        // 로컬 사용자일 경우 비밀번호 확인
        if(USER_TYPE_LOCAL.equals(userType)) {
            boolean isPasswordCorrect = mypageService.updatePersonalInformationWithPassword(password);

            if (!isPasswordCorrect) {
                redirectAttributes.addFlashAttribute(ERROR_ATTRIBUTE, "비밀번호가 일치하지 않습니다.");
                return "redirect:" + (referer != null ? referer : "/mypage/myinfo");
            }
        }

        mypageService.updatePersonalInformation(request);
        redirectAttributes.addFlashAttribute(MESSAGE_ATTRIBUTE, "정보가 성공적으로 수정되었습니다.");
        return "redirect:/mypage";
    }

    @GetMapping("/address")
    public String mypageAddressForm(Model model) {
        model.addAttribute("addresses", mypageService.getAllAddresses());
        return "mypage/address";
    }

    @GetMapping("/address-select")
    public String getAddressSelectPopup(Model model) {
        model.addAttribute("addresses", mypageService.getAllAddresses());
        return "mypage/address-select-popup";
    }

    @DeleteMapping("/address/{addressId}")
    public ResponseEntity<Void> mypageDeleteAddress(@PathVariable Long addressId) {
        mypageService.deleteAddress(addressId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/address/register")
    public String mypageAddAddress(@ModelAttribute AddressCreateRequest addressCreateRequest, RedirectAttributes redirectAttributes) {
        try {
            mypageService.addAddress(addressCreateRequest);
        } catch (FeignException.BadRequest e) {
            // 10개 초과로 등록 시
            redirectAttributes.addFlashAttribute(ERRORMESSAGE_ATTRIBUTE, "주소는 10개까지 등록 가능합니다.");
            return "redirect:/mypage/address"; // 주소 목록 페이지로 리다이렉트
        }
        return "redirect:/mypage/address";
    }

    @GetMapping("/address/register")
    public String mypageAddressRegisterForm() {
        return "mypage/address-register";
    }

    @GetMapping("/point")
    public String mypagePointForm(Pageable pageable, Model model) {

        Page<ResponsePoint> points = mypageService.getAllPoints(pageable);


        int newPoint = mypageService.getUserPoint();

        model.addAttribute("newPoint", newPoint);
        model.addAttribute("points", points.getContent());
        model.addAttribute("page", points);

        return "mypage/point";
    }

    @GetMapping("/myinfo")
    public String mypageInfo(HttpSession session, Model model) {
        String userType = (String) model.getAttribute(USER_TYPE_ATTRIBUTE);

        if(USER_TYPE_LOCAL.equals(userType)) {
            Boolean verified = (Boolean) session.getAttribute(MYPAGE_VERIFIED_ATTRIBUTE);
            if (verified == null || !verified) {
                return "redirect:/mypage/verify";
            }
        }

        // 1회성 인증으로 사용 후 플래그 제거
        session.removeAttribute(MYPAGE_VERIFIED_ATTRIBUTE);

        ResponseUser user = mypageService.getMyInfo();
        model.addAttribute("user", user);
        return "mypage/myinfo";
    }

    @GetMapping("/editpassword")
    public String mypageEditPasswordForm(Model model) {
        ResponseUser user = mypageService.getMyInfo();
        model.addAttribute("user", user);
        return "mypage/editpassword";
    }

    @PostMapping("/verify")
    public String verifyPassword(@RequestParam String password,
                                 HttpSession session,
                                 RedirectAttributes redirectAttributes) {
        if (!mypageService.updatePersonalInformationWithPassword(password)) {
            redirectAttributes.addFlashAttribute(ERROR_ATTRIBUTE, "비밀번호가 일치하지 않습니다.");
            return "redirect:/mypage/verify";
        }
        // 인증 성공 시 세션에 인증 플래그 설정
        session.setAttribute(MYPAGE_VERIFIED_ATTRIBUTE, true);
        return "redirect:/mypage/myinfo";
    }

    @GetMapping("/verify")
    public String mypageVerifyForm() {
        return "mypage/verify";
    }

    @GetMapping("/grade")
    public String mypageGradeForm(Model model) {
        ResponseUser user = mypageService.getMyInfo();
        ResponsePointType pointType = mypageService.getPointTypeByGradeName(user.getGradeName());
        model.addAttribute("pointType", pointType);
        model.addAttribute("user", user);
        return "mypage/grade";
    }

    @PostMapping("/grade/update")
    public String updateUserGrade() {
        mypageService.bulkUpdateUserGrades();
        return "redirect:/mypage/grade";
    }

    @GetMapping("/orders")
    public String mypageOrdersForm(Pageable pageable, Model model) {
        Page<OrderSummaryResponse> orders = mypageService.getAllOrders(pageable);
        model.addAttribute("orders", orders);
        return "mypage/order-list";
    }

    @GetMapping("/orders/{orderNumber}")
    public String getOrderDetail(@PathVariable String orderNumber, Model model) {
        OrderDetailResponse orderDetail = mypageService.getOrderDetail(orderNumber);
        model.addAttribute("order", orderDetail);
        return "mypage/order-detail";
    }

    @PostMapping("/orders/{orderNumber}/return")
    public String returnOrder(@PathVariable String orderNumber,
                              @ModelAttribute ReturnFormRequest formRequest,
                              RedirectAttributes redirectAttributes) {
        try {
            mypageService.returnOrder(orderNumber, formRequest.getReason(), formRequest.isDamaged());
            redirectAttributes.addFlashAttribute(MESSAGE_ATTRIBUTE, "반품 신청이 완료되었습니다.");
        } catch (Exception e) { // 모든 예외를 여기서 처리
            log.error("반품 신청 오류 발생: {}", e.getMessage());
            redirectAttributes.addFlashAttribute(ERRORMESSAGE_ATTRIBUTE, "반품 신청에 실패했습니다.");
        }
        return "redirect:/mypage/orders";
    }

    @PostMapping("/orders/{orderNumber}/cancel")
    public String cancelOrder(@PathVariable String orderNumber,
                              @RequestParam String reason,
                              RedirectAttributes redirectAttributes) {
        try {
            mypageService.cancelOrder(orderNumber, reason);
            redirectAttributes.addFlashAttribute(MESSAGE_ATTRIBUTE, "결제 취소가 완료되었습니다.");
        } catch (Exception e) {
            log.error("결제 취소 오류 발생: {}", e.getMessage());
            redirectAttributes.addFlashAttribute(ERRORMESSAGE_ATTRIBUTE, "결제 취소에 실패했습니다.");
        }
        return "redirect:/mypage/orders";
    }

    @GetMapping("/book-likes")
    public String mypageBookLikes(Pageable pageable, Model model) {
        Page<BookLikeResponse> bookLikesPage = mypageService.getBookLikes(pageable);
        model.addAttribute("bookLikes", bookLikesPage);
        return "mypage/book-likes";
    }
}
