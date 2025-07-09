package com.nhnacademy.frontend.admin.domain.response;

import java.util.List;

public record BookSearchResponseDto (
        int total,
        int start,
        int display,
        List<BookItemResponseDto> items

) {
}
