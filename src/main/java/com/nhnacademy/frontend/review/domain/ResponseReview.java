package com.nhnacademy.frontend.review.domain;


import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
//@AllArgsConstructor
public class ResponseReview {

    private long reviewId;
    private int evaluationScore;
    private String reviewContent;
    private List<String> reviewImages = new ArrayList<>();
    private LocalDateTime reviewedAt;
    private LocalDateTime updatedAt;
    private String userId;
    private long bookId;
    private String bookTitle;


    @JsonCreator
    public ResponseReview(
            @JsonProperty("reviewId") long reviewId,
            @JsonProperty("evaluationScore") int evaluationScore,
            @JsonProperty("reviewContent") String reviewContent,
            @JsonProperty("reviewImages") List<String> reviewImages,
            @JsonProperty("reviewedAt") LocalDateTime reviewedAt,
            @JsonProperty("updatedAt") LocalDateTime updatedAt,
            @JsonProperty("userId") String userId,
            @JsonProperty("bookId") long bookId,
            @JsonProperty("bookTitle") String bookTitle // Added parameter
    ) {
        this.reviewId = reviewId;
        this.evaluationScore = evaluationScore;
        this.reviewContent = reviewContent;
        this.reviewImages = reviewImages;
        this.reviewedAt = reviewedAt;
        this.updatedAt = updatedAt;
        this.userId = userId;
        this.bookId = bookId;
        this.bookTitle = bookTitle;
    }
}
