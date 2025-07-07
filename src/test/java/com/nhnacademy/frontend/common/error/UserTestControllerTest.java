package com.nhnacademy.frontend.common.error;

import com.nhnacademy.frontend.common.adapter.UserAdapter;
import com.nhnacademy.frontend.user.controller.UserTestController;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.ui.Model;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserTestControllerTest {

    @Mock
    UserAdapter userAdapter;

    @Mock
    Model model;

    @InjectMocks
    UserTestController controller;

    @Test
    void user_whenUserApiThrowsException_shouldPropagateException() {
        // given
        String exceptionMessage = "{\"status\":404,\"error\":\"Not Found\"}";
        when(userAdapter.getUser(anyString()))
                .thenThrow(new RuntimeException(exceptionMessage));

        // when & then
        RuntimeException ex = assertThrows(RuntimeException.class, () -> {
            controller.user("123", model);
        });
        assertTrue(ex.getMessage().contains("404"));
    }
}

