package com.nhnacademy.frontend.user.service;

import com.nhnacademy.frontend.common.adapter.UserAdapter;
import com.nhnacademy.frontend.user.domain.request.UserCreateRequestDto;
import com.nhnacademy.frontend.user.service.impl.UserServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.mockito.Mockito.*;
import static org.assertj.core.api.Assertions.*;

class UserServiceImplTest {

    private UserAdapter userAdapter;
    private UserServiceImpl userService;

    @BeforeEach
    void setUp() {
        userAdapter = mock(UserAdapter.class);
        userService = new UserServiceImpl(userAdapter);
    }

    @Test
    @DisplayName("register()는 UserAdapter의 registerUser()를 호출한다")
    void register_callsAdapter() {
        // given
        UserCreateRequestDto dto = mock(UserCreateRequestDto.class);

        // when
        userService.register(dto);

        // then
        verify(userAdapter, times(1)).registerUser(dto);
    }

    @Test
    @DisplayName("isExistUser()는 UserAdapter의 isExistUser() 결과를 반환한다")
    void isExistUser_returnsAdapterResult() {
        // given
        String userId = "testUser";
        when(userAdapter.isExistUser(userId)).thenReturn(true);

        // when
        boolean result = userService.isExistUser(userId);

        // then
        verify(userAdapter, times(1)).isExistUser(userId);
        assertThat(result).isTrue();
    }

    @Test
    @DisplayName("isExistUser()가 false를 반환하는 경우")
    void isExistUser_returnsFalse() {
        // given
        String userId = "notExists";
        when(userAdapter.isExistUser(userId)).thenReturn(false);

        // when
        boolean result = userService.isExistUser(userId);

        // then
        verify(userAdapter, times(1)).isExistUser(userId);
        assertThat(result).isFalse();
    }
}
