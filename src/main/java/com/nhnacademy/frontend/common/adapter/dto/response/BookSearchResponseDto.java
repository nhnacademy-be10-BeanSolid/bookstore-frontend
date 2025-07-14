package com.nhnacademy.frontend.common.adapter.dto.response;

import java.util.List;

public record BookSearchResponseDto (
        int total,
        int start,
        int display,
        List<BookItemResponseDto> items

) {
}
