package com.nhnacademy.frontend.auth.controller;

import com.nhnacademy.frontend.auth.controller.LoginController;
import com.nhnacademy.frontend.auth.filter.JwtAuthenticationFilter;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

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
}