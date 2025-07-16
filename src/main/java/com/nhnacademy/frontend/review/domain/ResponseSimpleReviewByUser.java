package com.nhnacademy.frontend.review.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ResponseSimpleReviewByUser {
    private long reviewId;
    private long bookId;
    private int evaluationScore;
    private LocalDateTime reviewedAt;
    private String reviewContent;

    public static ResponseSimpleReviewByUser fromResponseReview(ResponseReview responseReview) {
        return new ResponseSimpleReviewByUser(
                responseReview.getReviewId(),
                responseReview.getBookId(),
                responseReview.getEvaluationScore(),
                responseReview.getReviewedAt(),
                responseReview.getReviewContent()
        );
    }
}