package com.nhnacademy.frontend.admin.domain.response;

import java.time.LocalDateTime;

public record BookCategoryResponseDto (
        Long categoryId,
        String categoryName,
        Long parentId,
        String parentCategoryName,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
){
}