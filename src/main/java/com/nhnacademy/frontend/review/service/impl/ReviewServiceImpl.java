package com.nhnacademy.frontend.review.service.impl;

import com.nhnacademy.frontend.common.adapter.UserAdapter;
import com.nhnacademy.frontend.common.service.MinioService;
import com.nhnacademy.frontend.review.adapter.ReviewAdapter;
import com.nhnacademy.frontend.review.domain.*;
import com.nhnacademy.frontend.review.service.ReviewService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


@Slf4j
@Profile("!test")
@Service
@RequiredArgsConstructor
public class ReviewServiceImpl implements ReviewService {
    private final ReviewAdapter reviewAdapter;
    private final MinioService minioService;
    private final UserAdapter userAdapter;

    @Override
    public ResponseReview createReview(ReviewCreateRequest reviewCreateRequest, List<MultipartFile> images) {
        List<String> imageUrls = new ArrayList<>();
        if (images != null && !images.isEmpty()) {
            for (MultipartFile image : images) {
                if (!image.isEmpty()) {
                    String url = minioService.uploadImage(image);
                    imageUrls.add(url);
                }
            }
        }
        reviewCreateRequest.setImageUrls(imageUrls);
        return reviewAdapter.addReview(reviewCreateRequest).getBody();
    }

    @Override
    public ResponseReview getReview(long reviewId) {
        ResponseReview review = reviewAdapter.getReview(reviewId).getBody();
        String bookTitle = getTitleByBookId(Objects.requireNonNull(review).getBookId());
        review.setBookTitle(bookTitle);
        return review;
    }

    @Override
    public Page<ResponseSimpleReview> getReviewsByBookId(long bookId, Pageable pageable) {
        return reviewAdapter.getReviewByBookId(bookId, pageable).getBody();
    }

    @Override
    public Page<ResponseSimpleReviewByUser> getReviewsByUserId(String userId, Pageable pageable) {
        Page<ResponseSimpleReviewByUser> reviewsPage = reviewAdapter.getReviewByUserId(userId, pageable).getBody();
        Objects.requireNonNull(reviewsPage).forEach(review -> {
            String bookTitle = getTitleByBookId(review.getBookId());
            review.setBookTitle(bookTitle);
        });
        return reviewsPage;
    }

    @Override
    public ResponseReview editReview(long reviewId, ReviewUpdateRequest review, List<MultipartFile> images) {
        // 1. 기존 이미지 URL 목록을 가져옵니다. (null일 경우를 대비해 빈 리스트로 초기화)
        List<String> finalImageUrls = new ArrayList<>(review.getImageUrls() != null ? review.getImageUrls() : List.of());

        // 2. 새로 업로드된 이미지가 있으면 MinIO에 업로드하고 URL을 받아옵니다.
        if (images != null && !images.isEmpty()) {
            for (MultipartFile image : images) {
                if (image != null && !image.isEmpty()) {
                    String newUrl = minioService.uploadImage(image);
                    finalImageUrls.add(newUrl);
                }
            }
        }

        // 3. 최종 URL 목록으로 DTO를 업데이트합니다.
        ReviewUpdateRequest finalRequest = new ReviewUpdateRequest(
                review.getEvaluationScore(),
                review.getReviewContent(),
                finalImageUrls
        );

        // 4. user-api를 호출합니다.
        return reviewAdapter.updateReview(reviewId, finalRequest).getBody();
    }

    @Override
    public String getTitleByBookId(long bookId) {
        return reviewAdapter.getTitleByBookId(bookId).getBody();
    }

    @Override
    public boolean validatePurchase(String userId, Long bookId) {
        Long userNo = Objects.requireNonNull(userAdapter.getUserInfo().getBody()).getUserNo();
        return reviewAdapter.validatePurchase(userNo, bookId);
    }
}
