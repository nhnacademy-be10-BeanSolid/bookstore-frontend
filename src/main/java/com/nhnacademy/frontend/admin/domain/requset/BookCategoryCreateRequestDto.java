package com.nhnacademy.frontend.admin.domain.requset;

public record BookCategoryCreateRequestDto (
        String categoryName,
        Long parentId
) {
}
