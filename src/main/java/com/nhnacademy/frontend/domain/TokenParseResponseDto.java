package com.nhnacademy.frontend.domain;

import java.util.List;

public record TokenParseResponseDto(
        String username,
        List<String> authorities
) {
}
