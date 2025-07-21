package com.nhnacademy.frontend.review.domain;


import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReviewCreateRequest {
    @Min(1) @Max(5)
    private Integer evaluationScore;

    @NotBlank
    @Size(max = 255)
    private String reviewContent;

    private List<@Size(max = 2083) String> imageUrls;

    @NotBlank
    @Size(max = 20)
    private String userId;

    @Min(1)
    private long bookId;

}