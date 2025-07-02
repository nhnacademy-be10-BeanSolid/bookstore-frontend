package com.nhnacademy.frontend.book.domain.response;

public record BookCategoryUpdateRequestDto(
        String categoryName,
        Long parentId
) {
}
