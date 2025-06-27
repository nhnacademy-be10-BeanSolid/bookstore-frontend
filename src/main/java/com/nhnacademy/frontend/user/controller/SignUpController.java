package com.nhnacademy.frontend.user.controller;

import com.nhnacademy.frontend.user.domain.UserCreateRequestDto;
import com.nhnacademy.frontend.user.domain.UserIdCheckRequestDto;
import com.nhnacademy.frontend.user.service.UserService;
import com.nhnacademy.frontend.user.exception.ValidationFailedException;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/auth")
@RequiredArgsConstructor
public class SignUpController {

    private final UserService userService;

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

    // 중복확인 처리
    @PostMapping("/check-user-id")
    public String checkUserId(@ModelAttribute UserIdCheckRequestDto dto,
                              HttpSession session,
                              RedirectAttributes redirectAttributes) {
        boolean exists = userService.isExistUser(dto.getUserId());

        session.setAttribute("isAvailable", !exists);

        redirectAttributes.addFlashAttribute("userId", dto.getUserId());
        redirectAttributes.addFlashAttribute("duplicateMessage", exists ? "이미 사용 중인 아이디입니다." : "사용 가능한 아이디입니다.");

        return "redirect:/auth/normal-signup";
    }

    // 회원가입 처리
    @PostMapping("/normal-signup/register")
    public String registerUser(@ModelAttribute UserCreateRequestDto request,
                               BindingResult bindingResult,
                               HttpSession session,
                               RedirectAttributes redirectAttributes) {

        if (bindingResult.hasErrors()) {
            throw new ValidationFailedException(bindingResult);
        }

        Boolean isAvailable = (Boolean) session.getAttribute("isAvailable");

        if (isAvailable == null || !isAvailable) {
            redirectAttributes.addFlashAttribute("duplicateMessage", "아이디 중복 문제 먼저 해결해주세요.");
            redirectAttributes.addFlashAttribute("userId", request.userId());
            return "redirect:/auth/normal-signup";
        }

        userService.register(request);

        session.removeAttribute("isAvailable");

        redirectAttributes.addFlashAttribute("signupSuccess", true);
        return "redirect:/auth/login";
    }


}
