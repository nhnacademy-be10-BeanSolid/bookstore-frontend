package com.nhnacademy.frontend.auth.service;

import com.nhnacademy.frontend.auth.domain.LoginResponseDto;

public interface AuthService {
    LoginResponseDto login(String username, String password);
}
