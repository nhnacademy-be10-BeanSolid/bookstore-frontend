package com.nhnacademy.frontend.auth.domain.response;

public record PaycoCallbackResponseDto(
        String code,
        String state,
        String serviceExtra
) {
}
