package com.nhnacademy.frontend.domain;

public record PaycoCallbackResponseDto(
        String code,
        String state,
        String serviceExtra
) {
}
