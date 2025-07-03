package com.nhnacademy.frontend.auth.domain.request;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class OAuth2LoginRequestDto {
    private String provider;
    private String code;
}
