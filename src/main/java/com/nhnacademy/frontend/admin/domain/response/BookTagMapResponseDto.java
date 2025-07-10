package com.nhnacademy.frontend.admin.domain.response;

import java.util.List;

public record BookTagMapResponseDto (
        Long bookId,
        List<BookTagResponseDto> tags
)
{}