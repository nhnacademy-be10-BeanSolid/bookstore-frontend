package com.nhnacademy.frontend.mypage.controller;


import com.nhnacademy.frontend.auth.util.JwtCookieUtil;
import com.nhnacademy.frontend.common.adapter.domain.response.ResponseUser;
import com.nhnacademy.frontend.mypage.service.MypageService;
import com.nhnacademy.frontend.mypage.domain.request.UserUpdateRequestDto;
import com.nhnacademy.frontend.auth.util.JwtCookieUtil;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

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

    @PutMapping("/edit")
    @ResponseBody
    public ResponseEntity<Void> mypageEdit(@RequestBody UserUpdateRequestDto userUpdateRequestDto) {
        mypageService.updatePersonalInformation(userUpdateRequestDto);
        return ResponseEntity.ok().build();
    }
}
