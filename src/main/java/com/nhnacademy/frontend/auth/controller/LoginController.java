package com.nhnacademy.frontend.auth.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nhnacademy.frontend.auth.dto.request.DormantUserVerificationRequestDto;
import com.nhnacademy.frontend.auth.dto.request.NonMemberLoginRequest;
import com.nhnacademy.frontend.auth.dto.response.AdditionalSignupRequiredDto;
import com.nhnacademy.frontend.auth.dto.response.OAuth2LoginResponseDto;
import com.nhnacademy.frontend.auth.dto.response.PaycoCallbackResponseDto;
import com.nhnacademy.frontend.auth.dto.response.ResponseDto;
import com.nhnacademy.frontend.auth.service.AuthService;
import com.nhnacademy.frontend.auth.util.JwtCookieUtil;
import com.nhnacademy.frontend.common.service.UserService;
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
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.util.Objects;
import java.util.UUID;

@Controller
@RequestMapping("/auth/login")
@RequiredArgsConstructor
public class LoginController {
    private static final String REDIRECT_ROOT = "redirect:/";
    private static final String REDIRECT_LOGIN_FORM = "redirect:/auth/login";

    private final AuthService authService;
    private final JwtCookieUtil jwtCookieUtil;
    private final ObjectMapper objectMapper;
    private final UserService userService;

    @Value("${payco.client-id}")
    private String clientId;
    @Value("${payco.redirect-uri}")
    private String redirectUri;

    @GetMapping()
    public String showLoginForm(Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if(auth != null
            && auth.isAuthenticated()
            && !(auth instanceof AnonymousAuthenticationToken)) {
            return REDIRECT_ROOT;
        }
        if (model.containsAttribute("signupSuccess")) {
            model.addAttribute("signupSuccess", true);
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
            return REDIRECT_LOGIN_FORM;
        }

        String code = responseDto.code();

        ResponseDto<?> result = authService.oauth2Login("payco", code);

        if(result.isSuccess()) {
            OAuth2LoginResponseDto successData = objectMapper.convertValue(result.getData(), OAuth2LoginResponseDto.class);
            jwtCookieUtil.addJwtCookie(response, successData.getAccessToken(), successData.getRefreshToken());
            return REDIRECT_ROOT;
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

    @GetMapping("/dormant")
    public String showDormantForm(@RequestParam(name = "userId") String userId, Model model, RedirectAttributes redirectAttributes) {

        if(!userService.isDormantUser(userId)) {

            redirectAttributes.addFlashAttribute("accessDenied", "휴면 계정이 아니면 접근할 수 없습니다.");
            return REDIRECT_ROOT;
        }

        model.addAttribute("userId", userId);
        model.addAttribute("needVerification", "휴면 계정입니다, 인증코드를 입력해주세요.");

        return "auth/dormant";
    }

    @PostMapping("/dormant/verify")
    public String verifyDormantForm(@ModelAttribute DormantUserVerificationRequestDto dto, RedirectAttributes redirectAttributes) {

        if(authService.verifyDormantUserCode(dto)){

            redirectAttributes.addFlashAttribute("dormantSuccess", "인증성공! 휴면 상태가 해제되었습니다. 다시 로그인 해주세요.");
            return REDIRECT_LOGIN_FORM;
        }
        redirectAttributes.addFlashAttribute("dormantFail", "인증실패! 다시 인증해주세요.");
        return REDIRECT_LOGIN_FORM;
    }


    @PostMapping("/non-member")
    public String nonMemberLogin(@ModelAttribute NonMemberLoginRequest request,
                                 RedirectAttributes redirectAttributes) {

        boolean success = authService.nonMemberLogin(request);

        if (success) {
            redirectAttributes.addFlashAttribute("nonMemberOrderNumber", request.getOrderNumber());
            return "redirect:/orders/non-member-detail";
        } else {
            redirectAttributes.addFlashAttribute("nonMemberLoginError", "주문 정보를 찾을 수 없습니다.");
            return REDIRECT_LOGIN_FORM;
        }
    }
}
