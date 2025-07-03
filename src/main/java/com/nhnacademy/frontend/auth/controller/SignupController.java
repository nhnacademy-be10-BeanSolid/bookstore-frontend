package com.nhnacademy.frontend.auth.controller;


import com.nhnacademy.frontend.auth.domain.request.OAuth2AdditionalSignupRequestDto;
import com.nhnacademy.frontend.auth.domain.response.OAuth2LoginResponseDto;
import com.nhnacademy.frontend.auth.service.AuthService;
import com.nhnacademy.frontend.auth.util.JwtCookieUtil;
import com.nhnacademy.frontend.user.domain.request.UserCreateRequestDto;
import com.nhnacademy.frontend.user.domain.request.UserIdCheckRequestDto;
import com.nhnacademy.frontend.user.exception.ValidationFailedException;
import com.nhnacademy.frontend.user.service.UserService;
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

@Controller
@RequestMapping("/auth")
@RequiredArgsConstructor
public class SignupController {
    private final AuthService authService;
    private final JwtCookieUtil jwtCookieUtil;
    private final UserService userService;

    @PostMapping("/signup/oauth2")
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
    public String checkUserId(@ModelAttribute UserIdCheckRequestDto dto,
                              RedirectAttributes redirectAttributes) {
        boolean exists = userService.isExistUser(dto.getUserId());
        redirectAttributes.addFlashAttribute("isAvailable", !exists); // hidden input용 값 전달
        redirectAttributes.addFlashAttribute("userId", dto.getUserId());
        redirectAttributes.addFlashAttribute("duplicateMessage", exists ? "이미 사용 중인 아이디입니다." : "사용 가능한 아이디입니다.");
        return "redirect:/auth/normal-signup";
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
            return "redirect:/auth/normal-signup";
        }

        boolean exists = userService.isExistUser(request.userId());
        if (exists) {
            redirectAttributes.addFlashAttribute("duplicateMessage", "이미 사용 중인 아이디입니다.");
            redirectAttributes.addFlashAttribute("userId", request.userId());
            return "redirect:/auth/normal-signup";
        }

        userService.register(request);

        redirectAttributes.addFlashAttribute("signupSuccess", true);
        return "redirect:/auth/login";
    }

}