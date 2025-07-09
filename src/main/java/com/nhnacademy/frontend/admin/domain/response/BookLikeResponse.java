package com.nhnacademy.frontend.admin.domain.response;

import java.time.LocalDateTime;

public record BookLikeResponse (
        Long bookLikeId,
        LocalDateTime likedAt,
        String userId,
        Long bookId
) {
}
