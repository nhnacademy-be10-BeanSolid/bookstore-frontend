package com.nhnacademy.frontend.auth.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nhnacademy.frontend.auth.dto.request.DormantUserVerificationRequestDto;
import com.nhnacademy.frontend.auth.dto.request.NonMemberLoginRequest;
import com.nhnacademy.frontend.auth.dto.response.AdditionalSignupRequiredDto;
import com.nhnacademy.frontend.auth.dto.response.OAuth2LoginResponseDto;
import com.nhnacademy.frontend.auth.dto.response.ResponseDto;
import com.nhnacademy.frontend.auth.filter.JwtAuthenticationFilter;
import com.nhnacademy.frontend.auth.service.AuthService;
import com.nhnacademy.frontend.auth.util.JwtCookieUtil;
import com.nhnacademy.frontend.common.service.UserService;
import jakarta.servlet.http.Cookie;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
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
    UserService userService;

    @MockBean
    JwtCookieUtil jwtCookieUtil;

    @SpyBean
    ObjectMapper objectMapper;

    @MockBean
    private RedisConnectionFactory redisConnectionFactory;

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

    @Test
    @DisplayName("비회원 로그인 - 성공")
    void nonMemberLogin_success() throws Exception {
        NonMemberLoginRequest request = new NonMemberLoginRequest("order123", "password123");
        when(authService.nonMemberLogin(request)).thenReturn(true);

        mockMvc.perform(post("/auth/login/non-member")
                        .flashAttr("nonMemberLoginRequest", request))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/orders/non-member-detail"))
                .andExpect(flash().attribute("nonMemberOrderNumber", "order123"));
    }

    @Test
    @DisplayName("비회원 로그인 - 실패")
    void nonMemberLogin_failure() throws Exception {
        NonMemberLoginRequest request = new NonMemberLoginRequest("order123", "wrongpassword");
        when(authService.nonMemberLogin(request)).thenReturn(false);

        mockMvc.perform(post("/auth/login/non-member")
                        .flashAttr("nonMemberLoginRequest", request))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/auth/login"))
                .andExpect(flash().attribute("nonMemberLoginError", "주문 정보를 찾을 수 없습니다."));
    }

    @Test
    @DisplayName("휴면 계정 인증 폼 - 휴면 계정일 때 폼 반환")
    void showDormantForm_shouldReturnDormantForm() throws Exception {
        // userService.isDormantUser(...)가 true 반환하도록 mock
        when(userService.isDormantUser("dormantUser")).thenReturn(true);

        mockMvc.perform(get("/auth/login/dormant").param("userId", "dormantUser"))
                .andExpect(status().isOk())
                .andExpect(view().name("auth/dormant"))
                .andExpect(model().attribute("userId", "dormantUser"))
                .andExpect(model().attribute("needVerification", "휴면 계정입니다, 인증코드를 입력해주세요."));
    }

    @Test
    @DisplayName("휴면 계정 인증 폼 - 휴면계정이 아니면 리다이렉트")
    void showDormantForm_notDormantUser_redirectsToRoot() throws Exception {
        when(userService.isDormantUser("normalUser")).thenReturn(false);

        mockMvc.perform(get("/auth/login/dormant").param("userId", "normalUser"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/"))
                .andExpect(flash().attribute("accessDenied", "휴면 계정이 아니면 접근할 수 없습니다."));
    }

    @Test
    @DisplayName("휴면 계정 인증 - 성공")
    void verifyDormantForm_success() throws Exception {
        DormantUserVerificationRequestDto request = new DormantUserVerificationRequestDto("dormantUser", "123456");
        when(authService.verifyDormantUserCode(request)).thenReturn(true);

        mockMvc.perform(post("/auth/login/dormant/verify")
                        .flashAttr("dormantUserVerificationRequestDto", request))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/auth/login"))
                .andExpect(flash().attributeExists("dormantSuccess"));
    }

    @Test
    @DisplayName("휴면 계정 인증 - 실패")
    void verifyDormantForm_failure() throws Exception {
        DormantUserVerificationRequestDto request = new DormantUserVerificationRequestDto("dormantUser", "wrongCode");
        when(authService.verifyDormantUserCode(request)).thenReturn(false);

        mockMvc.perform(post("/auth/login/dormant/verify")
                        .flashAttr("dormantUserVerificationRequestDto", request))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/auth/login"))
                .andExpect(flash().attributeExists("dormantFail"));
    }
}