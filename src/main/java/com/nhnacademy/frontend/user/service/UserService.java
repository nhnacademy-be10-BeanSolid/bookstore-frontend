package com.nhnacademy.frontend.user.service;

import com.nhnacademy.frontend.user.domain.request.UserCreateRequestDto;

public interface UserService {

    void register(UserCreateRequestDto request);

    boolean isExistUser(String userId);
}
