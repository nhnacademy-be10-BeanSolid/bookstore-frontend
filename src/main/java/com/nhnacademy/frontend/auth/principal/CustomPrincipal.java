package com.nhnacademy.frontend.auth.principal;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.io.Serializable;

@Getter
@AllArgsConstructor
public class CustomPrincipal implements Serializable {
    private static final long serialVersionUID = 1L;
    private final String username;
    private final String userType;
}
