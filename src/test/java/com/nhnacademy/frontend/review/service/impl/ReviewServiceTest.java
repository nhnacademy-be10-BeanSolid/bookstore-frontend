package com.nhnacademy.frontend.review.service.impl;


import com.nhnacademy.frontend.common.adapter.UserAdapter;
import com.nhnacademy.frontend.common.service.MinioService;
import com.nhnacademy.frontend.review.adapter.ReviewAdapter;
import com.nhnacademy.frontend.review.domain.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReviewServiceTest {

    @Mock
    private ReviewAdapter reviewAdapter;

    @Mock
    private UserAdapter userAdapter;

    @Mock
    private MinioService minioService;


    @InjectMocks
    private ReviewServiceImpl reviewService;

    private ReviewCreateRequest reviewCreateRequest;
    private ResponseReview responseReview;
    private ReviewUpdateRequest reviewUpdateRequest;
    private Pageable pageable;

    @BeforeEach
    void setUp(){
        reviewCreateRequest = createReviewCreateRequest();
        responseReview = createResponseReview();
        reviewUpdateRequest = createReviewUpdateRequest();
        pageable = createPageable();
    }

    private ReviewCreateRequest createReviewCreateRequest(){
        return new ReviewCreateRequest(
                5,
                "This is a test review",
                List.of(new String[]{
                        "https://example.com/image1.jpg",
                        "https://example.com/image2.jpg"
                }),
                "user123",
                1L
        );
    }

    private ResponseReview createResponseReview() {
        return new ResponseReview(
                1L,
                5,
                "This is a test review",
                null,
                LocalDateTime.now(),
                null,
                "user123",
                1L,
                "Test Book Title"
        );
    }

    private ReviewUpdateRequest createReviewUpdateRequest(){
        return new ReviewUpdateRequest(
                4,
                "Updated review content",
                null
        );
    }

    private Pageable createPageable() {
        return Pageable.unpaged();
    }

    @Test
    @DisplayName("리뷰 생성 - 성공")
    void createReviewTest() {
        when(reviewAdapter.addReview(reviewCreateRequest))
                .thenReturn(ResponseEntity.ok(responseReview));
        reviewService.createReview(reviewCreateRequest, null);
        verify(reviewAdapter).addReview(reviewCreateRequest);
    }

    @Test
    @DisplayName("리뷰 생성 - 이미지 포함 성공")
    void createReviewWithImagesTest() {
        MultipartFile image1 = mock(MultipartFile.class);
        MultipartFile image2 = mock(MultipartFile.class);
        when(image1.isEmpty()).thenReturn(false);
        when(image2.isEmpty()).thenReturn(false);
        List<String> imageUrls = List.of("https://example.com/image1.jpg", "https://example.com/image2.jpg");
        when(minioService.uploadImage(image1)).thenReturn(imageUrls.get(0));
        when(minioService.uploadImage(image2)).thenReturn(imageUrls.get(1));
        when(reviewAdapter.addReview(reviewCreateRequest))
                .thenReturn(ResponseEntity.ok(responseReview));

        reviewService.createReview(reviewCreateRequest, List.of(image1, image2));
        verify(reviewAdapter).addReview(reviewCreateRequest);
        verify(minioService, times(2)).uploadImage(any(MultipartFile.class));
    }

    @Test
    @DisplayName("리뷰 조회 - 성공")
    void getReviewTest() {
        long reviewId = 1L;
        when(reviewAdapter.getReview(reviewId))
                .thenReturn(ResponseEntity.ok(responseReview));
        when(reviewAdapter.getTitleByBookId(1L))
                .thenReturn(ResponseEntity.ok("Test Book Title"));

        ResponseReview result = reviewService.getReview(reviewId);
        verify(reviewAdapter).getReview(reviewId);
        assert result != null;
        assert result.getReviewId() == reviewId;
    }

    @Test
    @DisplayName("책 ID로 리뷰 조회 - 성공")
    void getReviewsByBookIdTest() {
        long bookId = 1L;
        Page<ResponseSimpleReview> page = mock(Page.class);
        when(reviewAdapter.getReviewByBookId(bookId, pageable))
                .thenReturn(ResponseEntity.ok(page));
        Page<ResponseSimpleReview> result = reviewService.getReviewsByBookId(bookId, pageable);
        verify(reviewAdapter).getReviewByBookId(bookId, pageable);
        assertNotNull(result);
    }

    @Test
    @DisplayName("사용자 ID로 리뷰 조회 - 성공")
    void getReviewsByUserIdTest() {
        String userId = "user123";
        Page<ResponseSimpleReviewByUser> page = mock(Page.class);
        when(reviewAdapter.getReviewByUserId(userId, pageable))
                .thenReturn(ResponseEntity.ok(page));
        Page<ResponseSimpleReviewByUser> result = reviewService.getReviewsByUserId(userId, pageable);
        verify(reviewAdapter).getReviewByUserId(userId, pageable);
        assertNotNull(result);
    }

    @Test
    @DisplayName("리뷰 수정 - 성공")
    void editReviewTest() {
        long reviewId = 1L;
        List<MultipartFile> images = List.of(mock(MultipartFile.class));
        when(reviewAdapter.updateReview(eq(reviewId), any(ReviewUpdateRequest.class)))
                .thenReturn(ResponseEntity.ok(responseReview));
        when(minioService.uploadImage(any(MultipartFile.class)))
                .thenReturn("https://example.com/updated_image.jpg");

        ResponseReview result = reviewService.editReview(reviewId, reviewUpdateRequest, images);
        verify(reviewAdapter).updateReview(eq(reviewId), any(ReviewUpdateRequest.class));
        verify(minioService).uploadImage(any(MultipartFile.class));
        assertNotNull(result);
    }

    @Test
    @DisplayName("책 ID로 제목 조회 - 성공")
    void getTitleByBookIdTest() {
        long bookId = 1L;
        String expectedTitle = "Test Book Title";
        when(reviewAdapter.getTitleByBookId(bookId))
                .thenReturn(ResponseEntity.ok(expectedTitle));

        String result = reviewService.getTitleByBookId(bookId);
        verify(reviewAdapter).getTitleByBookId(bookId);
        assertNotNull(result);
        assert result.equals(expectedTitle);
    }

}
