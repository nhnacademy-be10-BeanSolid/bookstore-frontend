package com.nhnacademy.frontend.review.service;

import com.nhnacademy.frontend.review.domain.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface ReviewService {
    ResponseReview createReview(ReviewCreateRequest reviewCreateRequest,
                                List<MultipartFile> images);

    ResponseReview getReview(long reviewId);

    Page<ResponseSimpleReview>  getReviewsByBookId(long bookId, Pageable pageable);

    Page<ResponseSimpleReviewByUser> getReviewsByUserId(String userId, Pageable pageable);

    ResponseReview editReview(long reviewId, ReviewUpdateRequest review,
                              List<MultipartFile> images);

    String getTitleByBookId(long bookId);

    boolean validatePurchase(String userId, Long bookId);
}
