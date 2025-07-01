package com.nhnacademy.frontend.book.domain;

import java.util.Set;

public record BookCreateRequestDto(
        String title,
        String description,
        String toc,
        String publisher,
        String author,
        String publishAt,
        String isbn,
        Integer originalPrice,
        Integer salePrice,
        Boolean wrappable,
        Integer stock,
        Set<Long> categoryIds
) {}
