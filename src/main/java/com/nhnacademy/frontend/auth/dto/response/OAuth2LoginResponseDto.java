package com.nhnacademy.frontend.auth.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class OAuth2LoginResponseDto {
    private String accessToken;
    private String refreshToken;
}
