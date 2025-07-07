package com.nhnacademy.frontend.admin.domain.requset;

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
        String image,
        Set<Long> categoryIds
) {}
