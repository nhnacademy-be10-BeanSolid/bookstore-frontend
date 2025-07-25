package com.nhnacademy.frontend.common.adapter.dto.book.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

public record BookSearchResponseDto(
        // 최상위 dto
        String title, // API 결과의 제목
        Integer totalResults,
        Integer startIndex,
        Integer itemsPerPage,
        String query, // 검색어
        List<AladinItemDto> item // 상품정보
){
}
