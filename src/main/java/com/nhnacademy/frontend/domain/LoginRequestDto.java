package com.nhnacademy.frontend.domain;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class LoginRequestDto {
    private String id;
    private String password;
}
