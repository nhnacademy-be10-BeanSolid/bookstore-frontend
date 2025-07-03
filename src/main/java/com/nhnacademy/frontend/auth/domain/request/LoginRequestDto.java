package com.nhnacademy.frontend.auth.domain.request;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class LoginRequestDto {
    private String id;
    private String password;
}
