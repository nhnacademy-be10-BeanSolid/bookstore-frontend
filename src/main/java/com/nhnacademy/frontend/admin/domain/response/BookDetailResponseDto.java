package com.nhnacademy.frontend.admin.domain.response;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public record BookDetailResponseDto(
        Long id,
        String title,
        String description,
        String toc,
        String publisher,
        String author,
        LocalDate publishAt,
        String isbn,
        int originalPrice,
        int salePrice,
        Boolean wrappable,
        LocalDateTime createAt,
        LocalDateTime updateAt,
        String status,
        int stock,
        String image,

        List<BookCategoryResponseDto> bookCategories,
        List<BookTagResponseDto> bookTags,

        int likeCount
) {
}
