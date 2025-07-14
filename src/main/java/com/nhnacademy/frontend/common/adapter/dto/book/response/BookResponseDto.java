package com.nhnacademy.frontend.common.adapter.dto.book.response;

import java.time.LocalDate;
import java.util.Set;

public record BookResponseDto(
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
        boolean wrappable,
        String status,
        int stock,
        String image,

        Set<String> bookCategories,
        Set<String> bookTags
){
}