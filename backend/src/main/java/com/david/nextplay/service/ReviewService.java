package com.david.nextplay.service;

import java.time.LocalDateTime;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.david.nextplay.dto.review.CreateReviewRequest;
import com.david.nextplay.dto.review.ReviewResponse;
import com.david.nextplay.dto.review.UpdateReviewRequest;
import com.david.nextplay.entity.Game;
import com.david.nextplay.entity.LibraryEntry;
import com.david.nextplay.entity.Review;
import com.david.nextplay.entity.User;
import com.david.nextplay.enums.GameStatus;
import com.david.nextplay.exception.GameConflictException;
import com.david.nextplay.exception.ReviewConflictException;
import com.david.nextplay.exception.UserConflictException;
import com.david.nextplay.repository.GameRepository;
import com.david.nextplay.repository.LibraryEntryRepository;
import com.david.nextplay.repository.ReviewRepository;
import com.david.nextplay.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final UserRepository userRepository;
    private final GameRepository gameRepository;
    private final LibraryEntryRepository libraryEntryRepository;

    @Transactional(readOnly = true)
    public Page<ReviewResponse> getReviewsForGame(
            Long gameId,
            int page,
            int size,
            String sortBy,
            String sortDir) {
        Sort sort = sortDir.equalsIgnoreCase("desc")
                ? Sort.by(sortBy).descending()
                : Sort.by(sortBy).ascending();

        Pageable pageable = PageRequest.of(page, size, sort);

        Page<Review> reviews = reviewRepository.findByGameId(gameId, pageable);

        return reviews.map(this::mapToResponse);
    }

    @Transactional(readOnly = true)
    public Page<ReviewResponse> getMyGameReviews(
            Authentication authentication,
            int page,
            int size,
            String sortBy,
            String sortDir) {
        String email = authentication.getName();

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserConflictException("User not found."));

        Sort sort = sortBy.equalsIgnoreCase("desc")
                ? Sort.by(sortBy).descending()
                : Sort.by(sortBy).ascending();

        Pageable pageable = PageRequest.of(page, size, sort);

        Page<Review> reviews = reviewRepository.findByUserId(user.getId(), pageable);

        return reviews.map(this::mapToResponse);
    }

    @Transactional
    public ReviewResponse createGameReview(
            Authentication authentication,
            Long gameId,
            CreateReviewRequest request) {
        String email = authentication.getName();

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserConflictException("User not found."));

        Game game = gameRepository.findById(gameId)
                .orElseThrow(() -> new GameConflictException("Game not found."));

        boolean reviewExists = reviewRepository.existsByUserIdAndGameId(user.getId(), gameId);

        if (reviewExists) {
            throw new ReviewConflictException("A review already exists for " + game.getTitle());
        }

        LibraryEntry entry = libraryEntryRepository.findByUserIdAndGameId(user.getId(), gameId)
                .orElseThrow(() -> new ReviewConflictException("Game must be in library before creating a review."));

        if (entry.getGameStatus() == GameStatus.WANT_TO_PLAY) {
            throw new ReviewConflictException("You must play the game before creating a review.");
        }

        Review review = new Review();

        review.setUser(user);
        review.setGame(game);
        review.setRating(request.getRating());
        review.setComment(request.getComment());
        review.setCreatedAt(LocalDateTime.now());
        review.setUpdatedAt(LocalDateTime.now());

        Review savedReview = reviewRepository.save(review);

        updateGameAverageRating(game);

        return mapToResponse(savedReview);
    }

    @Transactional
    public ReviewResponse updateGameReview(
            Authentication authentication,
            Long id,
            UpdateReviewRequest request) {
        String email = authentication.getName();

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserConflictException("User not found."));

        Review review = reviewRepository.findByUserIdAndId(user.getId(), id)
                .orElseThrow(() -> new ReviewConflictException("Review not found."));

        review.setRating(request.getRating());
        review.setComment(request.getComment());
        review.setUpdatedAt(LocalDateTime.now());

        Review savedReview = reviewRepository.save(review);

        updateGameAverageRating(savedReview.getGame());

        return mapToResponse(savedReview);
    }

    @Transactional
    public void deleteGameReview(Authentication authentication, Long id) {
        String email = authentication.getName();

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserConflictException("User not found."));

        Review review = reviewRepository.findByUserIdAndId(user.getId(), id)
                .orElseThrow(() -> new ReviewConflictException("Review not found."));

        reviewRepository.delete(review);
        reviewRepository.flush();

        updateGameAverageRating(review.getGame());
    }

    private void updateGameAverageRating(Game game) {
        Double average = reviewRepository.findAverageRatingByGameId(game.getId());
        game.setAverageRating(average == null ? 0.0 : average);
        gameRepository.save(game);
    }

    private ReviewResponse mapToResponse(Review review) {
        return new ReviewResponse(
                review.getId(),
                review.getGame().getId(),
                review.getGame().getTitle(),
                review.getUser().getId(),
                review.getUser().getDisplayUsername(),
                review.getRating(),
                review.getComment(),
                review.getCreatedAt(),
                review.getUpdatedAt());
    }
}
