package com.nhnacademy.frontend.controller;


import com.nhnacademy.frontend.auth.domain.OAuth2AdditionalSignupRequestDto;
import com.nhnacademy.frontend.auth.domain.OAuth2LoginResponseDto;
import com.nhnacademy.frontend.auth.service.AuthService;
import com.nhnacademy.frontend.auth.util.JwtCookieUtil;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/auth/signup")
@RequiredArgsConstructor
public class SignupController {
    private final AuthService authService;
    private final JwtCookieUtil jwtCookieUtil;

    @PostMapping("/oauth2")
    public String signup(@ModelAttribute OAuth2AdditionalSignupRequestDto request,
                         HttpServletResponse response) {
        OAuth2LoginResponseDto result = authService.oauth2AdditionalSignup(request);

        jwtCookieUtil.addJwtCookie(response, result.getAccessToken(), result.getRefreshToken());

        return "redirect:/";
    }
}
