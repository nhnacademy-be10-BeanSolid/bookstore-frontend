package com.nhnacademy.frontend.book.domain.response;

import java.util.List;

public record BookSearchResponseDto (
        int total,
        int start,
        int display,
        List<BookItemResponseDto> items

) {
}
