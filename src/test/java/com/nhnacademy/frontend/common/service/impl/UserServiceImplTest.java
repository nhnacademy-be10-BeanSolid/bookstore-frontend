package com.nhnacademy.frontend.common.service.impl;

import com.nhnacademy.frontend.common.adapter.UserAdapter;
import com.nhnacademy.frontend.common.adapter.dto.user.response.ResponseUser;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserAdapter userAdapter;

    @InjectMocks
    private UserServiceImpl userService;

    private ResponseUser mockResponseUser;

    @BeforeEach
    void setUp() {
        mockResponseUser = ResponseUser.builder()
                .userPoint(1000)
                .build();
    }

    @Test
    @DisplayName("현재 사용자 포인트를 성공적으로 가져온다")
    void getCurrentUserPoints_success() {
        when(userAdapter.getUserInfo()).thenReturn(ResponseEntity.ok(mockResponseUser));

        Long points = userService.getCurrentUserPoints();

        assertEquals(1000L, points);
    }

    @Test
    @DisplayName("UserAdapter.getUserInfo()가 null을 반환할 때 0 포인트를 반환한다")
    void getCurrentUserPoints_userInfoResponseIsNull() {
        when(userAdapter.getUserInfo()).thenReturn(null);

        Long points = userService.getCurrentUserPoints();

        assertEquals(0L, points);
    }

    @Test
    @DisplayName("ResponseUser body가 null일 때 0 포인트를 반환한다")
    void getCurrentUserPoints_responseUserBodyIsNull() {
        when(userAdapter.getUserInfo()).thenReturn(ResponseEntity.ok(null));

        Long points = userService.getCurrentUserPoints();

        assertEquals(0L, points);
    }
}
