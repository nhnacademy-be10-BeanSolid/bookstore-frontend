package com.nhnacademy.frontend.auth.domain.response;

import java.util.List;

public record TokenParseResponseDto(
        String username,
        List<String> authorities,
        String userType
) {
}
