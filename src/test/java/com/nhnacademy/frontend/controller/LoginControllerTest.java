package com.nhnacademy.frontend.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nhnacademy.frontend.auth.controller.LoginController;
import com.nhnacademy.frontend.auth.domain.response.AdditionalSignupRequiredDto;
import com.nhnacademy.frontend.auth.domain.response.OAuth2LoginResponseDto;
import com.nhnacademy.frontend.auth.domain.response.ResponseDto;
import com.nhnacademy.frontend.auth.filter.JwtAuthenticationFilter;
import com.nhnacademy.frontend.auth.service.AuthService;
import com.nhnacademy.frontend.auth.util.JwtCookieUtil;
import jakarta.servlet.http.Cookie;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import static org.mockito.Mockito.doReturn;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(
        controllers = LoginController.class,
        excludeFilters = @ComponentScan.Filter(
                type = FilterType.ASSIGNABLE_TYPE,
                classes = JwtAuthenticationFilter.class
        )
)
@AutoConfigureMockMvc(addFilters = false)
class LoginControllerTest {
    @Autowired
    MockMvc mockMvc;

    @MockBean
    AuthService authService;

    @MockBean
    JwtCookieUtil jwtCookieUtil;

    @MockBean
    ObjectMapper objectMapper;

    @Test
    void showLoginForm_anonymousUser_returnsLoginForm() throws Exception {
        mockMvc.perform(get("/auth/login"))
                .andExpect(status().isOk())
                .andExpect(view().name("auth/login"));
    }

    @Test
    @WithMockUser(username = "user", roles = "USER")
    void showLoginForm_authenticationUser_redirectsToRoot() throws Exception {
        mockMvc.perform(get("/auth/login"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/"));
    }

    @Test
    @DisplayName("Payco 리다이렉트 테스트")
    void redirectToPaycoLogin_shouldRedirectToPaycoAuthUrl() throws Exception {
        mockMvc.perform(get("/auth/login/payco"))
                .andExpect(status().is3xxRedirection())
                .andExpect(header().exists("Set-Cookie"))
                .andExpect(redirectedUrlPattern("https://id.payco.com/oauth2.0/authorize*"));
    }

    @Test
    @DisplayName("Payco 콜백 (성공: 로그인)")
    void handlePaycoCallback_success_loginAndRedirectToHome() throws Exception {
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("code", "valid_code");
        params.add("state", "valid_state");

        OAuth2LoginResponseDto successData = new OAuth2LoginResponseDto("accessToken", "refreshToken");
        ResponseDto<?> successResponse = ResponseDto.builder()
                .success(true)
                .data(successData)
                .build();
        doReturn(successResponse).when(authService).oauth2Login("payco", "valid_code");
        doReturn(successData).when(objectMapper).convertValue(successResponse.getData(), OAuth2LoginResponseDto.class);

        mockMvc.perform(get("/auth/login/payco/callback")
                .params(params)
                .cookie(new Cookie("payco_oauth_state", "valid_state")))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/"));
    }

    @Test
    @DisplayName("Payco 콜백 (실패: 추가 회원가입 필요)")
    void handlePaycoCallback_failure_showSignupForm() throws Exception {
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("code", "valid_code");
        params.add("state", "valid_state");

        AdditionalSignupRequiredDto signupData = AdditionalSignupRequiredDto.builder()
                .tempJwt("tempJwt")
                .name("name")
                .email("email@test.com")
                .mobile("010-1234-5678")
                .mobileParts(new String[]{"010", "1234", "5678"})
                .build();

        ResponseDto<?> failResponse = ResponseDto.builder()
                .success(false)
                .data(signupData)
                .build();
        doReturn(failResponse).when(authService).oauth2Login("payco", "valid_code");
        doReturn(signupData).when(objectMapper).convertValue(failResponse.getData(), AdditionalSignupRequiredDto.class);

        mockMvc.perform(get("/auth/login/payco/callback")
                .params(params)
                .cookie(new Cookie("payco_oauth_state", "valid_state")))
                .andExpect(status().isOk())
                .andExpect(view().name("auth/oauth2-signup"))
                .andExpect(model().attribute("tempJwt", "tempJwt"))
                .andExpect(model().attribute("name", "name"))
                .andExpect(model().attribute("email", "email@test.com"))
                .andExpect(model().attribute("mobile1", "010"))
                .andExpect(model().attribute("mobile2", "1234"))
                .andExpect(model().attribute("mobile3", "5678"));
    }

    @Test
    @DisplayName("Payco 콜백 (State 불일치: 로그인 페이지로 리다이렉트)")
    void handlePaycoCallback_stateMismatch_redirectToLogin() throws Exception {
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("code", "valid_code");
        params.add("state", "invalid_state");

        mockMvc.perform(get("/auth/login/payco/callback")
                .params(params)
                .cookie(new Cookie("payco_oauth_state", "valid_state")))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/auth/login"));
    }

    @Test
    @DisplayName("Payco 콜백 (쿠키 없음: 로그인 페이지 리다이렉트)")
    void handlePaycoCallback_missingStateCookie_redirectToLogin() throws Exception {
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("code", "valid_code");
        params.add("state", "valid_state");

        mockMvc.perform(get("/auth/login/payco/callback")
                .params(params))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/auth/login"));
    }
}