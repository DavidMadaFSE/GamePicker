package com.david.nextplay.controller;

import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.david.nextplay.dto.review.CreateReviewRequest;
import com.david.nextplay.dto.review.ReviewResponse;
import com.david.nextplay.dto.review.UpdateReviewRequest;
import com.david.nextplay.service.ReviewService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class ReviewController {

    private final ReviewService reviewService;

    @GetMapping("/games/{gameId}/reviews")
    public ResponseEntity<Page<ReviewResponse>> getReviewsForGame(
            @PathVariable Long gameId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "updatedAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir) {
        Page<ReviewResponse> response = reviewService.getReviewsForGame(
                gameId,
                page,
                size,
                sortBy,
                sortDir);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/users/me/reviews")
    public ResponseEntity<Page<ReviewResponse>> getMyGameReviews(
            Authentication authentication,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "updatedAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir) {
        Page<ReviewResponse> response = reviewService.getMyGameReviews(
                authentication,
                page,
                size,
                sortBy,
                sortDir);

        return ResponseEntity.ok(response);
    }

    @PostMapping("/games/{gameId}/reviews")
    public ResponseEntity<ReviewResponse> createGameReview(
            Authentication authentication,
            @PathVariable Long gameId,
            @Valid @RequestBody CreateReviewRequest request) {
        ReviewResponse response = reviewService.createGameReview(authentication, gameId, request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    @PutMapping("/reviews/{id}")
    public ResponseEntity<ReviewResponse> updateGameReview(
            Authentication authentication,
            @PathVariable Long id,
            @RequestBody UpdateReviewRequest request) {
        ReviewResponse response = reviewService.updateGameReview(authentication, id, request);

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/reviews/{id}")
    public ResponseEntity<Void> deleteGameReview(
            Authentication authentication,
            @PathVariable Long id) {
        reviewService.deleteGameReview(authentication, id);

        return ResponseEntity
                .noContent()
                .build();
    }

}
