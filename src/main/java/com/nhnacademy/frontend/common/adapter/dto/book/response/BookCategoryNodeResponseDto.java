package com.nhnacademy.frontend.common.adapter.dto.book.response;

import java.util.List;

public record BookCategoryNodeResponseDto (
        Long categoryId,
        String categoryName,
        List<BookCategoryNodeResponseDto> children
) {
}