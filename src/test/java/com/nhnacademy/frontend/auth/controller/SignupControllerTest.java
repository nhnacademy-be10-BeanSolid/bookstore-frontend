package com.nhnacademy.frontend.auth.controller;

import com.nhnacademy.frontend.auth.domain.request.OAuth2AdditionalSignupRequestDto;
import com.nhnacademy.frontend.auth.domain.response.OAuth2LoginResponseDto;
import com.nhnacademy.frontend.auth.filter.JwtAuthenticationFilter;
import com.nhnacademy.frontend.auth.service.AuthService;
import com.nhnacademy.frontend.auth.util.JwtCookieUtil;
import com.nhnacademy.frontend.user.domain.request.UserCreateRequestDto;
import com.nhnacademy.frontend.user.service.UserService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
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
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.flash;

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
    private UserService userService;

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

    @DisplayName("회원가입 폼 GET - userIdCheckRequestDto 없는 경우")
    @Test
    void signupForm_addsUserIdCheckRequestDto() throws Exception {
        mockMvc.perform(get("/auth/normal-signup"))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("userIdCheckRequestDto"))
                .andExpect(view().name("auth/normal-signup"));
    }

    @DisplayName("회원가입 선택 폼 GET")
    @Test
    void showSelectSignupForm() throws Exception {
        mockMvc.perform(get("/auth/select-signup"))
                .andExpect(status().isOk())
                .andExpect(view().name("auth/select-signup"));
    }

    @DisplayName("아이디 중복확인 - 이미 존재하는 아이디")
    @Test
    void checkUserId_duplicate() throws Exception {
        given(userService.isExistUser("testuser")).willReturn(true);

        mockMvc.perform(post("/auth/check-user-id")
                        .param("userId", "testuser"))
                .andExpect(status().is3xxRedirection())
                .andExpect(flash().attribute("userId", "testuser"))
                .andExpect(flash().attribute("duplicateMessage", "이미 사용 중인 아이디입니다."))
                .andExpect(redirectedUrl("/auth/normal-signup"));
    }

    @DisplayName("아이디 중복확인 - 사용 가능한 아이디")
    @Test
    void checkUserId_available() throws Exception {
        given(userService.isExistUser("newuser")).willReturn(false);

        mockMvc.perform(post("/auth/check-user-id")
                        .param("userId", "newuser"))
                .andExpect(status().is3xxRedirection())
                .andExpect(flash().attribute("userId", "newuser"))
                .andExpect(flash().attribute("duplicateMessage", "사용 가능한 아이디입니다."))
                .andExpect(redirectedUrl("/auth/normal-signup"));
    }

    @DisplayName("회원가입 처리 - 중복확인 안함")
    @Test
    void registerUser_duplicateCheckNotPassed() throws Exception {
        mockMvc.perform(post("/auth/normal-signup/register")
                        .param("userId", "user1")
                        .param("userPassword", "pw1234")
                        .param("userName", "홍길동")
                        .param("isAvailable", "false"))
                .andExpect(status().is3xxRedirection())
                .andExpect(flash().attribute("duplicateMessage", "아이디 중복 문제 먼저 해결해주세요."))
                .andExpect(flash().attribute("userId", "user1"))
                .andExpect(redirectedUrl("/auth/normal-signup"));
    }

    @DisplayName("회원가입 처리 - 정상 플로우")
    @Test
    void registerUser_success() throws Exception {
        doNothing().when(userService).register(ArgumentMatchers.any(UserCreateRequestDto.class));

        mockMvc.perform(post("/auth/normal-signup/register")
                        .param("userId", "user2")
                        .param("userPassword", "pw1234")
                        .param("userName", "홍길동")
                        .param("isAvailable", "true"))
                .andExpect(status().is3xxRedirection())
                .andExpect(flash().attribute("signupSuccess", true))
                .andExpect(redirectedUrl("/auth/login"));
    }
}