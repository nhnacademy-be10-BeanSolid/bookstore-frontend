package com.nhnacademy.frontend.auth.controller;

import com.nhnacademy.frontend.auth.domain.request.OAuth2AdditionalSignupRequestDto;
import com.nhnacademy.frontend.auth.domain.response.OAuth2LoginResponseDto;
import com.nhnacademy.frontend.auth.filter.JwtAuthenticationFilter;
import com.nhnacademy.frontend.auth.service.AuthService;
import com.nhnacademy.frontend.auth.util.JwtCookieUtil;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(
        controllers = SignupController.class,
        excludeFilters = @ComponentScan.Filter(
                type = FilterType.ASSIGNABLE_TYPE,
                classes = JwtAuthenticationFilter.class
        )
)
@AutoConfigureMockMvc(addFilters = false)
class SignupControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AuthService authService;

    @MockBean
    private JwtCookieUtil jwtCookieUtil;

    @MockBean
    private RedisConnectionFactory redisConnectionFactory;

    @Test
    void signup_success() throws Exception {
        OAuth2LoginResponseDto responseDto = OAuth2LoginResponseDto.builder()
                .accessToken("access-token")
                .refreshToken("refresh-token")
                .build();

        when(authService.oauth2AdditionalSignup(any(OAuth2AdditionalSignupRequestDto.class)))
                .thenReturn(responseDto);

        mockMvc.perform(post("/auth/signup/oauth2")
                .param("tempJwt", "jwt")
                .param("name", "홍길동")
                .param("email", "hong@test.com")
                .param("mobile", "010-1111-2222")
                .param("birth", "1990-01-01")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/"));

        ArgumentCaptor<OAuth2AdditionalSignupRequestDto> captor = ArgumentCaptor.forClass(OAuth2AdditionalSignupRequestDto.class);
        verify(authService).oauth2AdditionalSignup(captor.capture());
        OAuth2AdditionalSignupRequestDto dto = captor.getValue();
        assertThat(dto.getName()).isEqualTo("홍길동");
        assertThat(dto.getEmail()).isEqualTo("hong@test.com");
        assertThat(dto.getMobile()).isEqualTo("010-1111-2222");
        assertThat(dto.getBirth()).isEqualTo("1990-01-01");

        verify(jwtCookieUtil).addJwtCookie(any(), eq("access-token"), eq("refresh-token"));
    }


}