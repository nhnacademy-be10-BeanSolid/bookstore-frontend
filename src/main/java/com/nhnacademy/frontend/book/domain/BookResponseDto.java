package com.nhnacademy.frontend.book.domain;

import java.util.Set;

public record BookResponseDto(
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

        Set<String> bookCategories,
        Set<String> bookTags
){
}