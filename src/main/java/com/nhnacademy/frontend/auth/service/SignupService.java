package com.nhnacademy.frontend.auth.service;

import com.nhnacademy.frontend.auth.domain.request.UserCreateRequestDto;

public interface SignupService {

    void register(UserCreateRequestDto request);

    boolean isExistUser(String userId);

}
