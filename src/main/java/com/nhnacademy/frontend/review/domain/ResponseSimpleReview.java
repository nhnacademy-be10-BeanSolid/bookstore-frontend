package com.nhnacademy.frontend.review.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ResponseSimpleReview {
    private long reviewId;
    private String userId;
    private int evaluationScore;
    private LocalDateTime reviewedAt;
    private String reviewContent;

    public static ResponseSimpleReview fromResponseReview(ResponseReview responseReview) {
        return new ResponseSimpleReview(
                responseReview.getReviewId(),
                responseReview.getUserId(),
                responseReview.getEvaluationScore(),
                responseReview.getReviewedAt(),
                responseReview.getReviewContent()
        );
    }
}
