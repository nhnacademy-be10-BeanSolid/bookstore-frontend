package com.nhnacademy.frontend.review.controller;

import com.nhnacademy.frontend.review.domain.*;
import com.nhnacademy.frontend.review.service.ReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Profile("!test")
@Controller
@RequestMapping("/reviews")
@RequiredArgsConstructor
public class ReviewController {
    private final ReviewService reviewService;

    @GetMapping("/form")
    public String showReviewForm(@RequestParam("bookId") long bookId,
                                 @ModelAttribute("loginUserId") String loginUserId,
                                 Model model) {

        boolean hasPurchased = reviewService.validatePurchase(loginUserId, bookId);
        model.addAttribute("bookId", bookId);
        model.addAttribute("hasPurchased", hasPurchased);
        return "review/reviewForm";
    }

    @PostMapping(value = "/create", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public String createReview(@RequestPart("review") ReviewCreateRequest reviewCreateRequest,
                               @RequestPart(value = "images", required = false) List<MultipartFile> images){
        reviewService.createReview(reviewCreateRequest, images);
        return "redirect:/";
    }

    @GetMapping("/edit/{reviewId}")
    public String editReviewForm(@PathVariable long reviewId,
                                 @ModelAttribute("loginUserId") String loginUserId,
                                 Model model) {
        ResponseReview review = reviewService.getReview(reviewId);
        if (loginUserId == null || loginUserId.isEmpty() || !loginUserId.equals(review.getUserId())) {
            return "redirect:/reviews/" + reviewId;
        }

        model.addAttribute("review", review);
        return "review/revieweditForm";
    }

    @PostMapping(value = "/edit/{reviewId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Void> editReview(@PathVariable long reviewId,
                                           @RequestPart("review") ReviewUpdateRequest review,
                                           @RequestPart(value = "images", required = false) List<MultipartFile> images) {
        try {
            reviewService.editReview(reviewId, review, images);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/{reviewId}")
    public String getReview(@PathVariable long reviewId, Model model) {
        model.addAttribute("review", reviewService.getReview(reviewId));
        return "review/reviewDetail";
    }

    @GetMapping("/book/{bookId}")
    public String getReviewsByBookId(@PathVariable long bookId,
                                     Pageable pageable,
                                     Model model) {
        Page<ResponseSimpleReview> reviews = reviewService.getReviewsByBookId(bookId, pageable);
        model.addAttribute("reviews", reviews.getContent());
        model.addAttribute("bookId", bookId);
        model.addAttribute("page", reviews);
        return "review/reviewByBook";
    }

    @GetMapping("/book/{bookId}/list-fragment")
    public String getReviewsFragment(@PathVariable long bookId, Pageable pageable, Model model) {
        Page<ResponseSimpleReview> reviews = reviewService.getReviewsByBookId(bookId, pageable);
        model.addAttribute("reviews", reviews.getContent());
        model.addAttribute("bookId", bookId);
        model.addAttribute("page", reviews);
        return "review/reviewListFragment :: reviewList";
    }

    @GetMapping("/user/{userId}")
    public String getReviewsByUserId(@PathVariable String userId,
                                     Pageable pageable,
                                     Model model) {
        Page<ResponseSimpleReviewByUser> reviews = reviewService.getReviewsByUserId(userId, pageable);
        model.addAttribute("reviews", reviews.getContent());
        model.addAttribute("userId", userId);
        model.addAttribute("page", reviews);
        return "review/reviewByUser";
    }
}
