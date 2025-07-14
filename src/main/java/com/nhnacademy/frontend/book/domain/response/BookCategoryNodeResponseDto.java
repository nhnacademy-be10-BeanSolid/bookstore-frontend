package com.nhnacademy.frontend.book.domain.response;

import java.util.List;

public record BookCategoryNodeResponseDto (
        Long categoryId,
        String categoryName,
        List<BookCategoryNodeResponseDto> children
) {
}