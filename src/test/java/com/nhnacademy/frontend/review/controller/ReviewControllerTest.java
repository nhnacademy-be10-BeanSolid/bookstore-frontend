package com.nhnacademy.frontend.review.controller;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.nhnacademy.frontend.auth.filter.JwtAuthenticationFilter;
import com.nhnacademy.frontend.review.domain.*;
import com.nhnacademy.frontend.review.service.ReviewService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = ReviewController.class,
        excludeFilters = @ComponentScan.Filter(
                type = FilterType.ASSIGNABLE_TYPE,
                classes = {JwtAuthenticationFilter.class}
        ))
@AutoConfigureMockMvc(addFilters = false)
public class ReviewControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ReviewService reviewService;

    @MockBean
    private RedisConnectionFactory redisConnectionFactory;

    private final ObjectMapper objectMapper = new ObjectMapper();
    private ResponseReview responseReview;

    private static final String ATTR_BOOK_ID = "bookId";

    private static final String ATTR_REVIEWS = "reviews";

    @BeforeEach
    void setUp(){
        responseReview = createResponseReview();
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

    private ResponseSimpleReview createResponseSimpleReview() {
        return ResponseSimpleReview.fromResponseReview(responseReview);
    }

    private ResponseSimpleReviewByUser createResponseSimpleReviewByUser() {
        return ResponseSimpleReviewByUser.fromResponseReview(responseReview);
    }



    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    @DisplayName("리뷰 생성 폼")
    void createReviewFormTest() throws Exception {
        mockMvc.perform(get("/reviews/form")
                .param(ATTR_BOOK_ID, String.valueOf(1L)))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists(ATTR_BOOK_ID))
                .andExpect(model().attributeExists("hasPurchased"));
    }

    @Test
    @DisplayName("리뷰 생성 - 멀티파트 요청")
    void createReviewTest() throws Exception {
        ReviewCreateRequest requestDto = new ReviewCreateRequest(5, "정말 좋은 책!", null, "user1", 1L);
        String reviewJson = objectMapper.writeValueAsString(requestDto);

        MockMultipartFile reviewPart = new MockMultipartFile("review", "", "application/json", reviewJson.getBytes());
        MockMultipartFile imageFile = new MockMultipartFile("images", "test-image.jpg", "image/jpeg", "test image content".getBytes());

        mockMvc.perform(multipart("/reviews/create")
                        .file(reviewPart)
                        .file(imageFile)
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/"));

        verify(reviewService).createReview(any(ReviewCreateRequest.class), any());
    }

    @Test
    @DisplayName("리뷰 수정 폼 - 성공")
    void editReviewForm_Success() throws Exception {
        long reviewId = 1L;
        String loginUserId = "user123";
        ResponseReview review = new ResponseReview(reviewId, 5, "Test review", null, LocalDateTime.now(), null, loginUserId, 1L, "Test Book Title");

        when(reviewService.getReview(reviewId)).thenReturn(review);

        mockMvc.perform(get("/reviews/edit/{reviewId}", reviewId)
                        .flashAttr("loginUserId", loginUserId))
                .andExpect(status().isOk())
                .andExpect(model().attribute("review", review))
                .andExpect(view().name("review/revieweditForm"));

        verify(reviewService).getReview(reviewId);
    }

    @Test
    @DisplayName("리뷰 수정 폼 - 실패 (사용자 불일치)")
    void editReviewForm_Failure_UserMismatch() throws Exception {
        long reviewId = 1L;
        String reviewOwnerId = "user123";
        String differentUserId = "user456";
        ResponseReview review = new ResponseReview(reviewId, 5, "Test review", null, LocalDateTime.now(), null, reviewOwnerId, 1L, "Test Book Title");

        when(reviewService.getReview(reviewId)).thenReturn(review);

        mockMvc.perform(get("/reviews/edit/{reviewId}", reviewId)
                        .flashAttr("loginUserId", differentUserId))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/reviews/" + reviewId));

        verify(reviewService).getReview(reviewId);
    }

    @Test
    @DisplayName("리뷰 수정 - 성공")
    void editReview_Success() throws Exception {
        long reviewId = 1L;
        ReviewUpdateRequest updateRequest = new ReviewUpdateRequest(4, "Updated review content", null);
        String updateJson = objectMapper.writeValueAsString(updateRequest);

        MockMultipartFile reviewPart = new MockMultipartFile("review", "", "application/json", updateJson.getBytes());
        MockMultipartFile imageFile = new MockMultipartFile("images", "test-image.jpg", "image/jpeg", "test image content".getBytes());

        mockMvc.perform(multipart("/reviews/edit/{reviewId}", reviewId)
                        .file(reviewPart)
                        .file(imageFile)
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isOk());

        verify(reviewService).editReview(eq(reviewId), any(ReviewUpdateRequest.class), any());
    }

    @Test
    @DisplayName("리뷰 수정 - 실패")
    void editReview_Failure_ServiceException() throws Exception {
        long reviewId = 1L;
        ReviewUpdateRequest updateRequest = new ReviewUpdateRequest(4, "Updated review content", null);
        String updateJson = objectMapper.writeValueAsString(updateRequest);

        MockMultipartFile reviewPart = new MockMultipartFile("review", "", "application/json", updateJson.getBytes());
        MockMultipartFile imageFile = new MockMultipartFile("images", "test-image.jpg", "image/jpeg", "test image content".getBytes());

        doThrow(new RuntimeException("Service error")).when(reviewService).editReview(eq(reviewId), any(ReviewUpdateRequest.class), any());

        mockMvc.perform(multipart("/reviews/edit/{reviewId}", reviewId)
                        .file(reviewPart)
                        .file(imageFile)
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isBadRequest());

        verify(reviewService).editReview(eq(reviewId), any(ReviewUpdateRequest.class), any());
    }

    @Test
    @DisplayName("리뷰 상세 조회")
    void getReviewTest() throws Exception {
        long reviewId = 1L;
        ResponseReview review = new ResponseReview(reviewId, 5, "Test review", null, LocalDateTime.now(), null, "user123", 1L, "Test Book Title");

        when(reviewService.getReview(reviewId)).thenReturn(review);

        mockMvc.perform(get("/reviews/{reviewId}", reviewId))
                .andExpect(status().isOk())
                .andExpect(model().attribute("review", review))
                .andExpect(view().name("review/reviewDetail"));

        verify(reviewService).getReview(reviewId);
    }

    @Test
    @DisplayName("리뷰 목록 조회 - 책 ID로")
    void getReviewsByBookIdTest() throws Exception {
        long bookId = 1L;
        Pageable pageable = Pageable.ofSize(10).withPage(0);
        List<ResponseSimpleReview> reviewList = List.of(createResponseSimpleReview());
        Page<ResponseSimpleReview> reviewPage = new PageImpl<>(reviewList, pageable, reviewList.size());

        when(reviewService.getReviewsByBookId(bookId, pageable)).thenReturn(reviewPage);

        mockMvc.perform(get("/reviews/book/{bookId}", bookId)
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(model().attribute(ATTR_REVIEWS, reviewPage.getContent()))
                .andExpect(model().attribute(ATTR_BOOK_ID, bookId))
                .andExpect(model().attribute("page", reviewPage))
                .andExpect(view().name("review/reviewByBook"));

        verify(reviewService).getReviewsByBookId(bookId, pageable);
    }

    @Test
    @DisplayName("리뷰 목록 프래그먼트 조회")
    void getReviewsFragmentTest() throws Exception {
        long bookId = 1L;
        Pageable pageable = Pageable.ofSize(5).withPage(1);
        List<ResponseSimpleReview> reviewList = List.of(createResponseSimpleReview());
        Page<ResponseSimpleReview> reviewPage = new PageImpl<>(reviewList, pageable, reviewList.size());

        when(reviewService.getReviewsByBookId(bookId, pageable)).thenReturn(reviewPage);

        mockMvc.perform(get("/reviews/book/{bookId}/list-fragment", bookId)
                        .param("page", "1")
                        .param("size", "5"))
                .andExpect(status().isOk())
                .andExpect(model().attribute(ATTR_REVIEWS, reviewPage.getContent()))
                .andExpect(model().attribute(ATTR_BOOK_ID, bookId))
                .andExpect(model().attribute("page", reviewPage))
                .andExpect(view().name("review/reviewListFragment :: reviewList"));

        verify(reviewService).getReviewsByBookId(bookId, pageable);
    }

    @Test
    @DisplayName("리뷰 목록 조회 - 사용자 ID로")
    void getReviewsByUserIdTest() throws Exception {
        String userId = "user123";
        Pageable pageable = Pageable.ofSize(10).withPage(0);
        List<ResponseSimpleReviewByUser> reviewList = List.of(createResponseSimpleReviewByUser());
        Page<ResponseSimpleReviewByUser> reviewPage = new PageImpl<>(reviewList, pageable, reviewList.size());

        when(reviewService.getReviewsByUserId(userId, pageable)).thenReturn(reviewPage);

        mockMvc.perform(get("/reviews/user/{userId}", userId)
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(model().attribute(ATTR_REVIEWS, reviewPage.getContent()))
                .andExpect(model().attribute("userId", userId))
                .andExpect(model().attribute("page", reviewPage))
                .andExpect(view().name("review/reviewByUser"));

        verify(reviewService).getReviewsByUserId(userId, pageable);
    }
}
