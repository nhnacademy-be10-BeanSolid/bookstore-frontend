package com.nhnacademy.frontend.admin.domain.response;

import java.util.List;

public record BookCategoryMapResponseDto (
        Long bookId,
        List<BookCategoryResponseDto> categories
) {
}
