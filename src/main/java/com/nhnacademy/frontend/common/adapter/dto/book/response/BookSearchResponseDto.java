package com.nhnacademy.frontend.common.adapter.dto.book.response;

import java.util.List;

public record BookSearchResponseDto (
        int total,
        int start,
        int display,
        List<BookItemResponseDto> items

) {
}
