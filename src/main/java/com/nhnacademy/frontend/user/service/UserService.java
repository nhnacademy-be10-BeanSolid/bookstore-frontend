package com.nhnacademy.frontend.user.service;

import com.nhnacademy.frontend.common.adapter.domain.response.ResponseUser;
import com.nhnacademy.frontend.user.domain.request.UserCreateRequestDto;
import com.nhnacademy.frontend.user.domain.request.UserUpdateRequestDto;

public interface UserService {

    void register(UserCreateRequestDto request);

    boolean isExistUser(String userId);

    void updatePersonalInformation(UserUpdateRequestDto request);

    ResponseUser getMyInfo();
}
