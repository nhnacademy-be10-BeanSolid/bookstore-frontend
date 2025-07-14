package com.nhnacademy.frontend.auth.dto.response;

public record PaycoCallbackResponseDto(
        String code,
        String state,
        String serviceExtra
) {
}
