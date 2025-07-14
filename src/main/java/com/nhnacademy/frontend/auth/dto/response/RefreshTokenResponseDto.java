package com.nhnacademy.frontend.auth.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class RefreshTokenResponseDto {
    private String accessToken;
    private String refreshToken;
}
