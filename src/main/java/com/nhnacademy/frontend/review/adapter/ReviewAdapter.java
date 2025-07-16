package com.nhnacademy.frontend.review.adapter;

import com.nhnacademy.frontend.review.domain.*;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@FeignClient(name = "gateway-service", contextId = "reviewAdapter")
public interface ReviewAdapter {
    @PostMapping("/user-api/reviews/me")
    ResponseEntity<ResponseReview> addReview(@RequestBody ReviewCreateRequest review);

    @PutMapping("/user-api/reviews/me/{reviewId}")
    ResponseEntity<ResponseReview> updateReview(@PathVariable("reviewId") long reviewId, @RequestBody ReviewUpdateRequest review);

    @GetMapping("/user-api/reviews/{reviewId}")
    ResponseEntity<ResponseReview> getReview(@PathVariable("reviewId") long reviewId);

    @GetMapping("/user-api/reviews/book/{bookId}")
    ResponseEntity<Page<ResponseSimpleReview>> getReviewByBookId(@PathVariable("bookId") long bookId, Pageable pageable);

    @GetMapping("/user-api/reviews/user/{userId}")
    ResponseEntity<Page<ResponseSimpleReviewByUser>> getReviewByUserId(@PathVariable("userId") String userId, Pageable pageable);
}