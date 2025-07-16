package com.nhnacademy.frontend.common.adapter.dto.book.response;

public record BookItemResponseDto(
        String title, // 상품명
        String link, // 개별 상품의 링크
        String image,
        String author,
        String publisher,
        String pubdate,
        String isbn,
        String description
) {
}
