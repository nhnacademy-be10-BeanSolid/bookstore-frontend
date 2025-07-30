package com.nhnacademy.frontend.common.adapter.dto.book.response;

public record AladinItemDto(
        String title, // 상품명
        String link, // 개별 상품의 링크
        String author,
        String pubDate,
        String description,
        String isbn, // 10자리
        String isbn13, // 13자리
        Integer priceSales, // 판매가
        Integer priceStandard, // 정가
        String cover,
        String publisher
) {
}