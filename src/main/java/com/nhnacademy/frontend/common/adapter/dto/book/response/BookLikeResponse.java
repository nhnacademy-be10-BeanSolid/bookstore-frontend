package com.nhnacademy.frontend.common.adapter.dto.book.response;

import java.time.LocalDateTime;

public record BookLikeResponse (
        Long bookLikeId,
        LocalDateTime likedAt,
        String userId,
        Long bookId
) {
}
