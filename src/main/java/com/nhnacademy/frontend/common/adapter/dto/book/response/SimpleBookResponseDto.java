package com.nhnacademy.frontend.common.adapter.dto.book.response;

public record SimpleBookResponseDto(
        Long id,
        String title,
        String author,
        Integer salePrice,
        Integer stock,
        String image,
        Long viewCount
) {
}