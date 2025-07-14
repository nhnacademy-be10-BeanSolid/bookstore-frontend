package com.nhnacademy.frontend.common.adapter.dto.book.response;

import java.util.List;

public record BookTagMapResponseDto (
        Long bookId,
        List<BookTagResponseDto> tags
)
{}