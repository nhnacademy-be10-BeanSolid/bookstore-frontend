package com.nhnacademy.frontend.admin.domain.response;

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
        String status,
        int stock,
        String image,

        Set<String> bookCategories,
        Set<String> bookTags
){
}