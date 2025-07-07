package com.nhnacademy.frontend.common.error;

import com.nhnacademy.frontend.auth.filter.JwtAuthenticationFilter;
import com.nhnacademy.frontend.common.adapter.UserAdapter;
import com.nhnacademy.frontend.user.controller.UserTestController;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(
        controllers = UserTestController.class,
        excludeFilters = @ComponentScan.Filter(
                type = FilterType.ASSIGNABLE_TYPE,
                classes = {JwtAuthenticationFilter.class}
        )
)
@AutoConfigureMockMvc(addFilters = false)
class UserTestControllerIntegrationTest {

    @MockBean
    UserAdapter userAdapter;

    @Autowired
    MockMvc mockMvc;

    @MockBean
    RedisConnectionFactory redisConnectionFactory;

    @Test
    void userApiThrowsException_shouldShowErrorPage() throws Exception {
        // given
        String exceptionMessage = "{\"status\":404,\"error\":\"Not Found\"}";
        when(userAdapter.getUser(anyString()))
                .thenThrow(new RuntimeException(exceptionMessage));

        // when & then
        mockMvc.perform(get("/users/123"))
                .andExpect(status().isOk())
                .andExpect(view().name("error/error"))
                .andExpect(model().attribute("statusCode", 404))
                .andExpect(model().attribute("userFriendlyMessage", "페이지를 찾을 수 없습니다."));
    }
}
