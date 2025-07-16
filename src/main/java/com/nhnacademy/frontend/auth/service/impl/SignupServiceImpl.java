package com.nhnacademy.frontend.auth.service.impl;

import com.nhnacademy.frontend.auth.service.SignupService;
import com.nhnacademy.frontend.common.adapter.UserAdapter;
import com.nhnacademy.frontend.common.adapter.dto.user.request.UserCreateRequestDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SignupServiceImpl implements SignupService {

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
