package com.nhnacademy.frontend.service;

import com.nhnacademy.frontend.domain.LoginResponseDto;

public interface AuthService {
    LoginResponseDto login(String username, String password);
}
