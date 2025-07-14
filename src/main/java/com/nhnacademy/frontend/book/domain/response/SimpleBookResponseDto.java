package com.nhnacademy.frontend.book.domain.response;

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