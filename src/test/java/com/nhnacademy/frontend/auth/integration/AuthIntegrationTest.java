package com.nhnacademy.frontend.auth.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nhnacademy.frontend.adapter.auth.AuthAdapter;
import com.nhnacademy.frontend.auth.domain.response.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockCookie;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class AuthIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AuthAdapter authAdapter;

    @SpyBean
    private ObjectMapper objectMapper;

    // 일반 로그인 성공 시나리오
    @Test
    void login_success_setsCookiesAndRedirects() throws Exception {
        LoginResponseDto loginResponseDto = new LoginResponseDto("access-token", "refresh-token");
        TokenParseResponseDto parseResponseDto = new TokenParseResponseDto("user1", List.of("ROLE_USER"));
        when(authAdapter.login(any())).thenReturn(loginResponseDto);
        when(authAdapter.parse(any())).thenReturn(parseResponseDto);

        mockMvc.perform(post("/auth/login")
                .param("username", "user1")
                .param("password", "pw123"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/"))
                .andExpect(cookie().exists("accessToken"))
                .andExpect(cookie().exists("refreshToken"));
    }

    // 일반 로그인 실패 시나리오
    @Test
    void login_failure_redirectsToLoginPage() throws Exception {
        when(authAdapter.login(any())).thenReturn(null);

        mockMvc.perform(post("/auth/login")
                .param("username", "invalid")
                .param("password", "wrong"))
                .andExpect(status().isUnauthorized())
                .andExpect(cookie().doesNotExist("accessToken"))
                .andExpect(cookie().doesNotExist("refreshToken"));
    }

    // 일반 로그아웃 시나리오
    @Test
    void logout_clearsCookiesAndRedirects() throws Exception {
        mockMvc.perform(post("/auth/logout"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/"))
                .andExpect(cookie().maxAge("accessToken", 0))
                .andExpect(cookie().maxAge("refreshToken", 0));
    }

    // 기존 회원 Payco 로그인 콜백 성공
    @Test
    void paycoCallback_existingUser_success() throws Exception {
        OAuth2LoginResponseDto loginResponse = new OAuth2LoginResponseDto("access-token", "refresh-token");
        ResponseDto<?> serviceResponse = ResponseDto.<OAuth2LoginResponseDto>builder()
                .success(true)
                .message(null)
                .data(loginResponse)
                .build();
        doReturn(serviceResponse).when(authAdapter).oauth2Login(any());

        mockMvc.perform(get("/auth/login/payco/callback")
                .param("code", "CODE")
                .param("state", "STATE")
                .cookie(new MockCookie("payco_oauth_state", "STATE")))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/"))
                .andExpect(cookie().exists("accessToken"))
                .andExpect(cookie().exists("refreshToken"));
    }

    // Payco 콜백 - 추가 회원가입 필요
    @Test
    void paycoCallback_additionalSignupRequired() throws Exception {
        AdditionalSignupRequiredDto signupDto = AdditionalSignupRequiredDto.builder()
                .tempJwt("temp-jwt")
                .name("홍길동")
                .email("hong@test.com")
                .mobile("010-1111-2222")
                .build();
        ResponseDto<?> serviceResponse = ResponseDto.<AdditionalSignupRequiredDto>builder()
                .success(false)
                .message(null)
                .data(signupDto)
                .build();
        doReturn(serviceResponse).when(authAdapter).oauth2Login(any());

        mockMvc.perform(get("/auth/login/payco/callback")
                .param("code", "CODE")
                .param("state", "STATE")
                .cookie(new MockCookie("payco_oauth_state", "STATE")))
                .andExpect(status().isOk())
                .andExpect(model().attribute("tempJwt", "temp-jwt"))
                .andExpect(model().attribute("name", "홍길동"))
                .andExpect(model().attribute("email", "hong@test.com"))
                .andExpect(model().attribute("mobile1", "010"))
                .andExpect(model().attribute("mobile2", "1111"))
                .andExpect(model().attribute("mobile3", "2222"));
    }

    // Payco 콜백 - 잘못된 state
    @Test
    void paycoCallback_invalidState_redirectsToLogin() throws Exception {
        mockMvc.perform(get("/auth/login/payco/callback")
                .param("code", "CODE")
                .param("state", "STATE")
                .cookie(new MockCookie("payco_oauth_state", "WRONG_STATE")))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/auth/login"));
    }

    // Payco 추가 회원가입 완료
    @Test
    void paycoAdditionalSignup_success() throws Exception {
        OAuth2LoginResponseDto loginResponse = new OAuth2LoginResponseDto("access-token", "refresh-token");

        when(authAdapter.additionalSignup(any())).thenReturn(loginResponse);

        mockMvc.perform(post("/auth/signup/oauth2")
                .param("tempJwt", "temp-jwt")
                .param("name", "홍길동")
                .param("email", "hong@test.com")
                .param("mobile", "010-1111-2222")
                .param("birth", "1990-01-01")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/"))
                .andExpect(cookie().exists("accessToken"))
                .andExpect(cookie().exists("refreshToken"));
    }
}
