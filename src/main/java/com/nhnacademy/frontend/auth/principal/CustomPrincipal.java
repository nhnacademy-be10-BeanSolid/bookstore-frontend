package com.nhnacademy.frontend.auth.principal;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class CustomPrincipal {
    private final String username;
    private final String userType;
}
