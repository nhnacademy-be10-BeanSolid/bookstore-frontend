package com.nhnacademy.frontend.common.adapter.dto.response;

import java.util.List;

public record BookCategoryMapResponseDto (
        Long bookId,
        List<BookCategoryResponseDto> categories
) {
}
