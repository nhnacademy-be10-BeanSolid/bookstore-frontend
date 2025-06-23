package com.nhnacademy.frontend.auth.domain;

import java.util.List;

public record TokenParseResponseDto(
        String username,
        List<String> authorities
) {
}
