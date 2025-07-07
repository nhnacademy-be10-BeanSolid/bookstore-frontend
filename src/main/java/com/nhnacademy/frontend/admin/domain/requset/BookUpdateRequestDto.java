package com.nhnacademy.frontend.admin.domain.requset;

public record BookUpdateRequestDto (
        String title,
        String description,
        String toc,
        String author,
        String publisher,
        String publishAt,
        Integer originalPrice,
        Integer salePrice,
        Boolean wrappable,
        Integer stock,
        String status
){
}
