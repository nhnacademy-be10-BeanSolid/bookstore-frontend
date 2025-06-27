package com.nhnacademy.frontend.controller;

import com.nhnacademy.frontend.auth.domain.LoginResponseDto;
import com.nhnacademy.frontend.auth.service.AuthService;
import com.nhnacademy.frontend.auth.util.JwtCookieUtil;
import com.nhnacademy.frontend.domain.PaycoCallbackResponseDto;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;

import java.io.IOException;
import java.util.UUID;

@Controller
@RequestMapping("/auth")
@RequiredArgsConstructor
public class LoginController {
    private final AuthService authService;
    private final JwtCookieUtil jwtCookieUtil;

    @Value("${payco.client-id}")
    private String clientId;
    @Value("${payco.redirect-uri}")
    private String redirectUri;

    @GetMapping("/login")
    public String showLoginForm() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if(auth != null
            && auth.isAuthenticated()
            && !(auth instanceof AnonymousAuthenticationToken)) {
            return "redirect:/";
        }
        return "auth/login";
    }

    @GetMapping("/login/payco")
    public void redirectToPaycoLogin(HttpServletResponse response) throws IOException {
        String state = UUID.randomUUID().toString();

//        Cookie stateCookie = new Cookie("payco_oauth_state", state);
//        stateCookie.setHttpOnly(true);
//        stateCookie.setSecure(true);
//        stateCookie.setPath("/");
//        stateCookie.setMaxAge(180);
//        stateCookie.setSameSite("Strict");

//        response.addCookie(stateCookie);

        String paycoAuthUrl = "https://id.payco.com/oauth2.0/authorize?response_type=code"
                + "&client_id=" + clientId
                + "&serviceProviderCode=FRIENDS"
                + "&redirect_uri=" + redirectUri
                + "&state=" + state
                + "&userLocale=ko_KR";

        response.sendRedirect(paycoAuthUrl);
    }

    @GetMapping("/login/payco/callback")
    public String handlePaycoCallback(@ModelAttribute PaycoCallbackResponseDto responseDto,
                                      HttpServletResponse response) {
        String code = responseDto.code();

        LoginResponseDto loginResponse = authService.oauth2Login("payco", code);

        String accessToken = loginResponse.getAccessToken();
        String refreshToken = loginResponse.getRefreshToken();
        jwtCookieUtil.addJwtCookie(response, accessToken, refreshToken);

        return "redirect:/";
    }
}
