package com.nhnacademy.frontend.book.domain;

public record BookCategoryUpdateRequestDto(
        String categoryName,
        Long parentId
) {
}
