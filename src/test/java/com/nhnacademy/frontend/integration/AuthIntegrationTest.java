package com.nhnacademy.frontend.integration;

import com.nhnacademy.frontend.adapter.AuthAdapter;
import com.nhnacademy.frontend.auth.domain.LoginResponseDto;
import com.nhnacademy.frontend.auth.domain.TokenParseResponseDto;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class AuthIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AuthAdapter authAdapter;

    // 1. 로그인 성공 시나리오
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

    // 2. 로그인 실패 시나리오
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

    // 3. 로그아웃 시나리오
    @Test
    void logout_clearsCookiesAndRedirects() throws Exception {
        mockMvc.perform(post("/auth/logout"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/"))
                .andExpect(cookie().maxAge("accessToken", 0))
                .andExpect(cookie().maxAge("refreshToken", 0));
    }
}
