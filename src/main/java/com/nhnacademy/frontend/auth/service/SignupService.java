package com.nhnacademy.frontend.auth.service;

import com.nhnacademy.frontend.common.adapter.dto.user.request.UserCreateRequestDto;

public interface SignupService {

    void register(UserCreateRequestDto request);

    boolean isExistUser(String userId);

}
