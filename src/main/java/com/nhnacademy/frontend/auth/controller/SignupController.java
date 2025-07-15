package com.nhnacademy.frontend.auth.controller;


import com.nhnacademy.frontend.auth.dto.request.OAuth2AdditionalSignupRequestDto;
import com.nhnacademy.frontend.auth.dto.response.OAuth2LoginResponseDto;
import com.nhnacademy.frontend.auth.service.AuthService;
import com.nhnacademy.frontend.auth.service.SignupService;
import com.nhnacademy.frontend.auth.util.JwtCookieUtil;
import com.nhnacademy.frontend.common.adapter.dto.user.request.UserCreateRequestDto;
import com.nhnacademy.frontend.auth.dto.request.UserIdCheckRequestDto;
import com.nhnacademy.frontend.common.exception.ValidationFailedException;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Map;

@Controller
@RequestMapping("/auth/signup")
@RequiredArgsConstructor
public class SignupController {
    private final AuthService authService;
    private final JwtCookieUtil jwtCookieUtil;
    private final SignupService signupService;

    @PostMapping("/oauth2")
    public String signup(@ModelAttribute OAuth2AdditionalSignupRequestDto request,
                         HttpServletResponse response) {
        OAuth2LoginResponseDto result = authService.oauth2AdditionalSignup(request);

        jwtCookieUtil.addJwtCookie(response, result.getAccessToken(), result.getRefreshToken());

        return "redirect:/";
    }

    @GetMapping("/normal-signup")
    public String signupForm(Model model) {
        if (!model.containsAttribute("userIdCheckRequestDto")) {
            model.addAttribute("userIdCheckRequestDto", new UserIdCheckRequestDto());
        }
        return "auth/normal-signup";
    }

    @GetMapping("/select-signup")
    public String showSelectSignupForm() {

        return "auth/select-signup";
    }

    @PostMapping("/check-user-id")
    @ResponseBody
    public Map<String, Object> checkUserId(@ModelAttribute UserIdCheckRequestDto dto) {
        boolean exists = signupService.isExistUser(dto.getUserId());
        return Map.of(
                "isAvailable", !exists,
                "message", exists ? "이미 사용 중인 아이디입니다." : "사용 가능한 아이디입니다."
        );
    }

    @PostMapping("/normal-signup/register")
    public String registerUser(@ModelAttribute UserCreateRequestDto request,
                               BindingResult bindingResult,
                               RedirectAttributes redirectAttributes) {

        if (bindingResult.hasErrors()) {
            throw new ValidationFailedException(bindingResult);
        }

        Boolean isAvailable = request.isAvailable();
        if (isAvailable == null || !isAvailable) {
            redirectAttributes.addFlashAttribute("duplicateMessage", "아이디 중복 문제 먼저 해결해주세요.");
            redirectAttributes.addFlashAttribute("userId", request.userId());
            return "redirect:/auth/signup/normal-signup";
        }

        boolean exists = signupService.isExistUser(request.userId());
        if (exists) {
            redirectAttributes.addFlashAttribute("duplicateMessage", "이미 사용 중인 아이디입니다.");
            redirectAttributes.addFlashAttribute("userId", request.userId());
            return "redirect:/auth/signup/normal-signup";
        }

        signupService.register(request);

        redirectAttributes.addFlashAttribute("signupSuccess", true);
        return "redirect:/auth/login";
    }

}