package com.nhnacademy.frontend.book.domain.response;

import java.util.List;

public record BookDetailResponseDto(
        Long id,
        String title,
        String description,
        String toc,
        String publisher,
        String author,
        String publishAt,
        String isbn,
        int originalPrice,
        int salePrice,
        Boolean wrappable,
        String createAt,
        String updateAt,
        String status,
        int stock,
        String image,

        List<BookCategoryResponseDto> bookCategories,
        List<BookTagResponseDto> bookTags,

        int likeCount
) {
}
