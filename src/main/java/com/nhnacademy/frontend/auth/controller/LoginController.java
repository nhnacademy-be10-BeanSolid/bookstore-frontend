package com.nhnacademy.frontend.auth.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nhnacademy.frontend.auth.domain.response.AdditionalSignupRequiredDto;
import com.nhnacademy.frontend.auth.domain.response.OAuth2LoginResponseDto;
import com.nhnacademy.frontend.auth.domain.response.PaycoCallbackResponseDto;
import com.nhnacademy.frontend.auth.domain.response.ResponseDto;
import com.nhnacademy.frontend.auth.service.AuthService;
import com.nhnacademy.frontend.auth.util.JwtCookieUtil;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;

import java.io.IOException;
import java.util.Objects;
import java.util.UUID;

@Controller
@RequestMapping("/auth/login")
@RequiredArgsConstructor
public class LoginController {
    private final AuthService authService;
    private final JwtCookieUtil jwtCookieUtil;
    private final ObjectMapper objectMapper;

    @Value("${payco.client-id}")
    private String clientId;
    @Value("${payco.redirect-uri}")
    private String redirectUri;

    @GetMapping()
    public String showLoginForm() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if(auth != null
            && auth.isAuthenticated()
            && !(auth instanceof AnonymousAuthenticationToken)) {
            return "redirect:/";
        }
        return "auth/login";
    }

    @GetMapping("/payco")
    public void redirectToPaycoLogin(HttpServletResponse response) throws IOException {
        String state = UUID.randomUUID().toString();

        Cookie stateCookie = new Cookie("payco_oauth_state", state);
        stateCookie.setHttpOnly(true);
        stateCookie.setSecure(true);
        stateCookie.setPath("/");
        stateCookie.setMaxAge(180);

        response.addCookie(stateCookie);

        String paycoAuthUrl = "https://id.payco.com/oauth2.0/authorize?response_type=code"
                + "&client_id=" + clientId
                + "&serviceProviderCode=FRIENDS"
                + "&redirect_uri=" + redirectUri
                + "&state=" + state
                + "&userLocale=ko_KR";

        response.sendRedirect(paycoAuthUrl);
    }

    @GetMapping("/payco/callback")
    public String handlePaycoCallback(@ModelAttribute PaycoCallbackResponseDto responseDto,
                                      HttpServletRequest request,
                                      HttpServletResponse response,
                                      Model model) {
        String cookieState = null;
        if(request.getCookies() != null) {
            for(Cookie cookie : request.getCookies()) {
                if("payco_oauth_state".equals(cookie.getName())) {
                    cookieState = cookie.getValue();
                    break;
                }
            }
        }

        String stateParam = responseDto.state();
        if(!Objects.equals(stateParam, cookieState) || cookieState == null) {
            return "redirect:/auth/login";
        }

        String code = responseDto.code();

        ResponseDto<?> result = authService.oauth2Login("payco", code);

        if(result.isSuccess()) {
            OAuth2LoginResponseDto successData = objectMapper.convertValue(result.getData(), OAuth2LoginResponseDto.class);
            jwtCookieUtil.addJwtCookie(response, successData.getAccessToken(), successData.getRefreshToken());
            return "redirect:/";
        } else {
            AdditionalSignupRequiredDto signupData = objectMapper.convertValue(result.getData(), AdditionalSignupRequiredDto.class);
            model.addAttribute("tempJwt", signupData.getTempJwt());
            model.addAttribute("name", signupData.getName());
            model.addAttribute("email", signupData.getEmail());

            if(signupData.getMobileParts() != null) {
                model.addAttribute("mobile1", signupData.getMobileParts()[0]);
                model.addAttribute("mobile2", signupData.getMobileParts()[1]);
                model.addAttribute("mobile3", signupData.getMobileParts()[2]);
            }
            return "auth/oauth2-signup";
        }
    }
}
