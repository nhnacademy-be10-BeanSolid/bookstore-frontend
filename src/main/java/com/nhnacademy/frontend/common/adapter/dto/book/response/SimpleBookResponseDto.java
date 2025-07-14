package com.nhnacademy.frontend.common.adapter.dto.book.response;

public record SimpleBookResponseDto(
        long id,
        String title,
        String author,
        int salePrice,
        int stock,
        String image
) {
}