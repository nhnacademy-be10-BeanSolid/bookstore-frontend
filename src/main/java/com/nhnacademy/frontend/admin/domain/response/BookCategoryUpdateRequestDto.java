package com.nhnacademy.frontend.admin.domain.response;

public record BookCategoryUpdateRequestDto(
        String categoryName,
        Long parentId
) {
}
