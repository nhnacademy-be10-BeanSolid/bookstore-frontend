package com.nhnacademy.frontend.common.adapter.dto.book.response;

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