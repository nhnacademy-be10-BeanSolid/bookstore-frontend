package com.nhnacademy.frontend.user.service.impl;

import com.nhnacademy.frontend.adapter.user.UserAdapter;
import com.nhnacademy.frontend.user.domain.UserCreateRequestDto;
import com.nhnacademy.frontend.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserAdapter userAdapter;

    @Override
    public void register(UserCreateRequestDto request) {
        userAdapter.registerUser(request);
    }

    @Override
    public boolean isExistUser(String userId) {
        return userAdapter.isExistUser(userId);
    }

}
