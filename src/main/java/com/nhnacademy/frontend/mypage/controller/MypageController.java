package com.nhnacademy.frontend.mypage.controller;


import com.nhnacademy.frontend.auth.util.JwtCookieUtil;
import com.nhnacademy.frontend.common.adapter.domain.response.ResponsePointType;
import com.nhnacademy.frontend.common.adapter.domain.response.ResponseUser;
import com.nhnacademy.frontend.mypage.domain.request.AddressCreateRequest;
import com.nhnacademy.frontend.mypage.domain.request.UserUpdateRequestDto;
import com.nhnacademy.frontend.mypage.service.MypageService;
import feign.FeignException;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Objects;

@Controller
@RequestMapping("/mypage")
@RequiredArgsConstructor
public class MypageController {
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
        String userType = (String) model.getAttribute("userType");

        boolean result;

        if("OAUTH2".equals(userType)) {
            result = mypageService.withdrawOAuth2User();
        } else if ("LOCAL".equals(userType)) {
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
    public String editMyPage(@RequestParam String password,
                             @RequestParam(required = false) String userPassword,
                             @RequestParam(required = false) String userPasswordConfirm,
                             @ModelAttribute UserUpdateRequestDto request,
                             RedirectAttributes redirectAttributes,
                             @RequestHeader(value = "Referer", required = false) String referer) {
        if (userPassword != null || userPasswordConfirm != null) {
            if (!Objects.equals(userPassword, userPasswordConfirm)) {
                redirectAttributes.addFlashAttribute("error", "수정할 비밀번호와 비밀번호 확인이 일치하지 않습니다.");
                return "redirect:" + (referer != null ? referer : "/mypage/edit");
            }
        }
        boolean isPasswordCorrect = mypageService.updatePersonalInformationWithPassword(password);

        if (!isPasswordCorrect) {
            redirectAttributes.addFlashAttribute("error", "비밀번호가 일치하지 않습니다.");
            return "redirect:" + (referer != null ? referer : "/mypage/myinfo");
        }

        mypageService.updatePersonalInformation(request);
        redirectAttributes.addFlashAttribute("message", "정보가 성공적으로 수정되었습니다.");
        return "redirect:/mypage/myinfo";
    }

    @GetMapping("/address")
    public String mypageAddressForm(Model model) {
        model.addAttribute("addresses", mypageService.getAllAddresses());
        return "mypage/address";
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
            redirectAttributes.addFlashAttribute("errorMessage", "주소는 10개까지 등록 가능합니다.");
            return "redirect:/mypage/address"; // 주소 목록 페이지로 리다이렉트
        }
        return "redirect:/mypage/address";
    }

    @GetMapping("/address/register")
    public String mypageAddressRegisterForm() {
        return "mypage/address_register";
    }

    @GetMapping("/myinfo")
    public String mypageInfo(HttpSession session, Model model) {
        String userType = (String) model.getAttribute("userType");

        if("LOCAL".equals(userType)) {
            Boolean verified = (Boolean) session.getAttribute("mypage_verified");
            if (verified == null || !verified) {
                return "redirect:/mypage/verify";
            }
        }

        // 1회성 인증으로 사용 후 플래그 제거
        session.removeAttribute("mypage_verified");

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
            redirectAttributes.addFlashAttribute("error", "비밀번호가 일치하지 않습니다.");
            return "redirect:/mypage/verify";
        }
        // 인증 성공 시 세션에 인증 플래그 설정
        session.setAttribute("mypage_verified", true);
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
}
