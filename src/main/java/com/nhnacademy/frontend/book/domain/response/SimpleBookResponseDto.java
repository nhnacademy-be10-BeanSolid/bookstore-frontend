package com.nhnacademy.frontend.book.domain.response;

public record SimpleBookResponseDto(
        long id,
        String title,
        String author,
        int salePrice,
        int stock,
        String image
) {
}