package com.david.nextplay.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;

import com.david.nextplay.dto.review.CreateReviewRequest;
import com.david.nextplay.dto.review.ReviewResponse;
import com.david.nextplay.dto.review.UpdateReviewRequest;
import com.david.nextplay.entity.Game;
import com.david.nextplay.entity.LibraryEntry;
import com.david.nextplay.entity.Review;
import com.david.nextplay.entity.User;
import com.david.nextplay.enums.GameStatus;
import com.david.nextplay.enums.Genre;
import com.david.nextplay.enums.Platform;
import com.david.nextplay.enums.Role;
import com.david.nextplay.exception.GameConflictException;
import com.david.nextplay.exception.ReviewConflictException;
import com.david.nextplay.exception.UserConflictException;
import com.david.nextplay.repository.GameRepository;
import com.david.nextplay.repository.LibraryEntryRepository;
import com.david.nextplay.repository.ReviewRepository;
import com.david.nextplay.repository.UserRepository;

@ExtendWith(MockitoExtension.class)
public class ReviewServiceTest {

    @Mock
    private ReviewRepository reviewRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private GameRepository gameRepository;

    @Mock
    private LibraryEntryRepository libraryEntryRepository;

    @Mock
    private Authentication authentication;

    @InjectMocks
    private ReviewService reviewService;

    private User user;
    private User secondUser;
    private Game game;
    private LibraryEntry libraryEntry;
    private LibraryEntry secondLibraryEntry;
    private Review userReview;
    private Review secondUserReview;
    private Page<Review> reviewPage;
    private CreateReviewRequest createReviewRequest;
    private UpdateReviewRequest updateReviewRequest;

    @BeforeEach
    void setUp() {
        user = new User();
        secondUser = new User();
        game = new Game();
        libraryEntry = new LibraryEntry();
        secondLibraryEntry = new LibraryEntry();
        userReview = new Review();
        secondUserReview = new Review();
        reviewPage = new PageImpl<>(List.of(userReview, secondUserReview));
        createReviewRequest = new CreateReviewRequest();
        updateReviewRequest = new UpdateReviewRequest();

        createReviewRequest.setRating(5);
        createReviewRequest.setComment("Davids review");

        updateReviewRequest.setRating(4);
        updateReviewRequest.setComment("Updated review");

        user.setId(1L);
        user.setUsername("davidmada1");
        user.setEmail("david@example.com");
        user.setPassword("encodedPassword");
        user.setRole(Role.USER);
        user.setCreatedAt(LocalDateTime.of(2026, 06, 12, 12, 0));
        user.setUpdatedAt(LocalDateTime.of(2026, 06, 12, 12, 0));

        secondUser.setId(2L);
        secondUser.setUsername("admin");
        secondUser.setEmail("admin@example.com");
        secondUser.setPassword("encodedPassword");
        secondUser.setRole(Role.ADMIN);
        secondUser.setCreatedAt(LocalDateTime.of(2026, 06, 12, 12, 0));
        secondUser.setUpdatedAt(LocalDateTime.of(2026, 06, 12, 12, 0));

        game.setId(1L);
        game.setTitle("Minecraft");
        game.setDescription(
                "A sandbox survival game where players build, explore, and craft in block-based worlds.");
        game.setCoverImageUrl("http://minecraft.jpg");
        game.setReleaseDate(LocalDate.of(2011, 11, 17));
        game.setAverageRating(5.0);
        game.setPlatforms(
                List.of(Platform.PC, Platform.PLAYSTATION, Platform.XBOX, Platform.NINTENDO_SWITCH, Platform.MOBILE));
        game.setGenres(List.of(Genre.ADVENTURE, Genre.SIMULATION));

        libraryEntry.setId(1L);
        libraryEntry.setGame(game);
        libraryEntry.setUser(user);
        libraryEntry.setGameStatus(GameStatus.WANT_TO_PLAY);
        libraryEntry.setCreatedAt(LocalDateTime.of(2026, 06, 11, 12, 00));
        libraryEntry.setUpdatedAt(LocalDateTime.of(2026, 06, 11, 12, 00));

        secondLibraryEntry.setId(2L);
        secondLibraryEntry.setGame(game);
        secondLibraryEntry.setUser(secondUser);
        secondLibraryEntry.setGameStatus(GameStatus.PLAYING);
        secondLibraryEntry.setCreatedAt(LocalDateTime.of(2026, 07, 11, 12, 30));
        secondLibraryEntry.setUpdatedAt(LocalDateTime.of(2026, 07, 11, 12, 30));

        userReview.setId(1L);
        userReview.setUser(user);
        userReview.setGame(game);
        userReview.setComment("Davids review");
        userReview.setRating(5);
        userReview.setCreatedAt(LocalDateTime.of(2026, 06, 11, 12, 0));
        userReview.setUpdatedAt(LocalDateTime.of(2026, 06, 11, 12, 0));

        secondUserReview.setId(1L);
        secondUserReview.setUser(secondUser);
        secondUserReview.setGame(game);
        secondUserReview.setComment("Admins review");
        secondUserReview.setRating(1);
        secondUserReview.setCreatedAt(LocalDateTime.of(2026, 06, 11, 12, 0));
        secondUserReview.setUpdatedAt(LocalDateTime.of(2026, 06, 11, 12, 0));
    }

    @Test
    void getReviewsForGame_WhenMethodIsCalled_ShouldReturnPageReviewResponse() {
        // Arrange
        when(reviewRepository.findByGameId(
                eq(game.getId()),
                any(Pageable.class))).thenReturn(reviewPage);

        // Act
        Page<ReviewResponse> response = reviewService.getReviewsForGame(
                game.getId(),
                0,
                10,
                "updatedAt",
                "asc");

        // Assert
        assertNotNull(response);
        assertEquals(2, response.getContent().size());
        assertEquals(user.getId(), response.getContent().get(0).getUserId());
        assertEquals(secondUser.getId(), response.getContent().get(1).getUserId());

        // Verify
        verify(reviewRepository).findByGameId(
                eq(game.getId()),
                any(Pageable.class));
    }

    @Test
    void getMyGameReviews_WhenUserExists_ShouldReturnPageReviewResponse() {
        // Arrange
        when(authentication.getName()).thenReturn(user.getEmail());
        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));
        when(reviewRepository.findByUserId(
                eq(user.getId()),
                any(Pageable.class))).thenReturn(reviewPage);

        // Act
        Page<ReviewResponse> response = reviewService.getMyGameReviews(
                authentication,
                0,
                10,
                "updatedAt",
                "asc");

        // Assert
        assertNotNull(response);
        assertEquals(2, response.getContent().size());
        assertEquals(userReview.getId(), response.getContent().get(0).getId());
        assertEquals(secondUserReview.getId(), response.getContent().get(1).getId());

        // Verify
        verify(authentication).getName();
        verify(userRepository).findByEmail(user.getEmail());
        verify(reviewRepository).findByUserId(
                eq(user.getId()),
                any(Pageable.class));
    }

    @Test
    void getMyGameReviews_WhenUserDoesNotExist_ShouldThrowUserConflictException() {
        // Arrange
        when(authentication.getName()).thenReturn(user.getEmail());
        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.empty());

        // Act + Assert
        assertThrows(UserConflictException.class, () -> {
            reviewService.getMyGameReviews(
                    authentication,
                    0,
                    10,
                    "updatedAt",
                    "asc");
        });

        // Verify
        verify(authentication).getName();
        verify(userRepository).findByEmail(user.getEmail());
        verify(reviewRepository, never()).findByUserId(anyLong(), any(Pageable.class));
    }

    @Test
    void createGameReview_WhenUserDoesNotExist_ShouldThrowUserConflictException() {
        // Arrange
        when(authentication.getName()).thenReturn(user.getEmail());
        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.empty());

        // Act + Assert
        assertThrows(UserConflictException.class, () -> {
            reviewService.createGameReview(
                    authentication,
                    game.getId(),
                    createReviewRequest);
        });

        // Verify
        verify(authentication).getName();
        verify(userRepository).findByEmail(user.getEmail());
        verify(gameRepository, never()).findById(anyLong());
        verify(reviewRepository, never()).save(any(Review.class));
    }

    @Test
    void createGameReview_WhenGameDoesNotExist_ShouldThrowGameConflictException() {
        // Arrange
        when(authentication.getName()).thenReturn(user.getEmail());
        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));
        when(gameRepository.findById(game.getId())).thenReturn(Optional.empty());

        // Act + Assert
        assertThrows(GameConflictException.class, () -> {
            reviewService.createGameReview(
                    authentication,
                    game.getId(),
                    createReviewRequest);
        });

        // Verify
        verify(authentication).getName();
        verify(userRepository).findByEmail(user.getEmail());
        verify(gameRepository).findById(game.getId());
        verify(reviewRepository, never()).existsByUserIdAndGameId(anyLong(), anyLong());
        verify(reviewRepository, never()).save(any(Review.class));
    }

    @Test
    void createGameReview_WhenReviewAlreadyExists_ShouldThrowReviewConflictException() {
        // Arrange
        when(authentication.getName()).thenReturn(user.getEmail());
        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));
        when(gameRepository.findById(game.getId())).thenReturn(Optional.of(game));
        when(reviewRepository.existsByUserIdAndGameId(
                user.getId(),
                game.getId())).thenReturn(true);

        // Act + Assert
        assertThrows(ReviewConflictException.class, () -> {
            reviewService.createGameReview(
                    authentication,
                    game.getId(),
                    createReviewRequest);
        });

        // Verify
        verify(authentication).getName();
        verify(userRepository).findByEmail(user.getEmail());
        verify(gameRepository).findById(game.getId());
        verify(reviewRepository).existsByUserIdAndGameId(user.getId(), game.getId());
        verify(libraryEntryRepository, never()).findByUserIdAndGameId(anyLong(), anyLong());
        verify(reviewRepository, never()).save(any(Review.class));
    }

    @Test
    void createGameReview_WhenGameIsNotInLibrary_ShouldThrowReviewConflictException() {
        // Arrange
        when(authentication.getName()).thenReturn(user.getEmail());
        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));
        when(gameRepository.findById(game.getId())).thenReturn(Optional.of(game));
        when(reviewRepository.existsByUserIdAndGameId(
                user.getId(),
                game.getId())).thenReturn(false);
        when(libraryEntryRepository.findByUserIdAndGameId(
                user.getId(),
                game.getId())).thenReturn(Optional.empty());

        // Act + Assert
        assertThrows(ReviewConflictException.class, () -> {
            reviewService.createGameReview(
                    authentication,
                    game.getId(),
                    createReviewRequest);
        });

        // Verify
        verify(authentication).getName();
        verify(userRepository).findByEmail(user.getEmail());
        verify(gameRepository).findById(game.getId());
        verify(reviewRepository).existsByUserIdAndGameId(user.getId(), game.getId());
        verify(libraryEntryRepository).findByUserIdAndGameId(user.getId(), game.getId());
        verify(reviewRepository, never()).save(any(Review.class));
    }

    @Test
    void createGameReview_WhenGameStatusIsWantToPlay_ShouldThrowReviewConflictException() {
        // Arrange
        when(authentication.getName()).thenReturn(user.getEmail());
        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));
        when(gameRepository.findById(game.getId())).thenReturn(Optional.of(game));
        when(reviewRepository.existsByUserIdAndGameId(
                user.getId(),
                game.getId())).thenReturn(false);
        when(libraryEntryRepository.findByUserIdAndGameId(
                user.getId(),
                game.getId())).thenReturn(Optional.of(libraryEntry));

        // Act + Assert
        assertThrows(ReviewConflictException.class, () -> {
            reviewService.createGameReview(
                    authentication,
                    game.getId(),
                    createReviewRequest);
        });

        // Verify
        verify(authentication).getName();
        verify(userRepository).findByEmail(user.getEmail());
        verify(gameRepository).findById(game.getId());
        verify(reviewRepository).existsByUserIdAndGameId(user.getId(), game.getId());
        verify(libraryEntryRepository).findByUserIdAndGameId(user.getId(), game.getId());
        verify(reviewRepository, never()).save(any(Review.class));
    }

    @Test
    void createGameReview_WhenReviewCanBeCreated_ShouldReturnReviewResponse() {
        // Arrange
        libraryEntry.setGameStatus(GameStatus.COMPLETED);
        when(authentication.getName()).thenReturn(user.getEmail());
        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));
        when(gameRepository.findById(game.getId())).thenReturn(Optional.of(game));
        when(reviewRepository.existsByUserIdAndGameId(
                user.getId(),
                game.getId())).thenReturn(false);
        when(libraryEntryRepository.findByUserIdAndGameId(
                user.getId(),
                game.getId())).thenReturn(Optional.of(libraryEntry));
        when(reviewRepository.save(any(Review.class))).thenReturn(userReview);
        when(reviewRepository.findAverageRatingByGameId(game.getId())).thenReturn(5.0);

        // Act
        ReviewResponse response = reviewService.createGameReview(
                authentication,
                game.getId(),
                createReviewRequest);

        // Assert
        assertNotNull(response);
        assertEquals(userReview.getId(), response.getId());
        assertEquals(game.getId(), response.getGameId());
        assertEquals(user.getId(), response.getUserId());
        assertEquals(createReviewRequest.getRating(), response.getRating());
        assertEquals(createReviewRequest.getComment(), response.getComment());
        assertEquals(5.0, game.getAverageRating());

        // Verify
        verify(authentication).getName();
        verify(userRepository).findByEmail(user.getEmail());
        verify(gameRepository).findById(game.getId());
        verify(reviewRepository).existsByUserIdAndGameId(user.getId(), game.getId());
        verify(libraryEntryRepository).findByUserIdAndGameId(user.getId(), game.getId());
        verify(reviewRepository).save(any(Review.class));
        verify(reviewRepository).findAverageRatingByGameId(game.getId());
        verify(gameRepository).save(game);
    }

    @Test
    void updateGameReview_WhenUserDoesNotExist_ShouldThrowUserConflictException() {
        // Arrange
        when(authentication.getName()).thenReturn(user.getEmail());
        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.empty());

        // Act + Assert
        assertThrows(UserConflictException.class, () -> {
            reviewService.updateGameReview(
                    authentication,
                    userReview.getId(),
                    updateReviewRequest);
        });

        // Verify
        verify(authentication).getName();
        verify(userRepository).findByEmail(user.getEmail());
        verify(reviewRepository, never()).findByUserIdAndId(anyLong(), anyLong());
        verify(reviewRepository, never()).save(any(Review.class));
    }

    @Test
    void updateGameReview_WhenReviewDoesNotExist_ShouldThrowReviewConflictException() {
        // Arrange
        when(authentication.getName()).thenReturn(user.getEmail());
        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));
        when(reviewRepository.findByUserIdAndId(
                user.getId(),
                userReview.getId())).thenReturn(Optional.empty());

        // Act + Assert
        assertThrows(ReviewConflictException.class, () -> {
            reviewService.updateGameReview(
                    authentication,
                    userReview.getId(),
                    updateReviewRequest);
        });

        // Verify
        verify(authentication).getName();
        verify(userRepository).findByEmail(user.getEmail());
        verify(reviewRepository).findByUserIdAndId(user.getId(), userReview.getId());
        verify(reviewRepository, never()).save(any(Review.class));
    }

    @Test
    void updateGameReview_WhenReviewExists_ShouldReturnReviewResponse() {
        // Arrange
        when(authentication.getName()).thenReturn(user.getEmail());
        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));
        when(reviewRepository.findByUserIdAndId(
                user.getId(),
                userReview.getId())).thenReturn(Optional.of(userReview));
        when(reviewRepository.save(any(Review.class))).thenReturn(userReview);
        when(reviewRepository.findAverageRatingByGameId(game.getId())).thenReturn(4.0);

        // Act
        ReviewResponse response = reviewService.updateGameReview(
                authentication,
                userReview.getId(),
                updateReviewRequest);

        // Assert
        assertNotNull(response);
        assertEquals(userReview.getId(), response.getId());
        assertEquals(updateReviewRequest.getRating(), response.getRating());
        assertEquals(updateReviewRequest.getComment(), response.getComment());
        assertEquals(4.0, game.getAverageRating());

        // Verify
        verify(authentication).getName();
        verify(userRepository).findByEmail(user.getEmail());
        verify(reviewRepository).findByUserIdAndId(user.getId(), userReview.getId());
        verify(reviewRepository).save(userReview);
        verify(reviewRepository).findAverageRatingByGameId(game.getId());
        verify(gameRepository).save(game);
    }

    @Test
    void deleteGameReview_WhenUserDoesNotExist_ShouldThrowUserConflictException() {
        // Arrange
        when(authentication.getName()).thenReturn(user.getEmail());
        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.empty());

        // Act + Assert
        assertThrows(UserConflictException.class, () -> {
            reviewService.deleteGameReview(authentication, userReview.getId());
        });

        // Verify
        verify(authentication).getName();
        verify(userRepository).findByEmail(user.getEmail());
        verify(reviewRepository, never()).findByUserIdAndId(anyLong(), anyLong());
        verify(reviewRepository, never()).delete(any(Review.class));
    }

    @Test
    void deleteGameReview_WhenReviewDoesNotExist_ShouldThrowReviewConflictException() {
        // Arrange
        when(authentication.getName()).thenReturn(user.getEmail());
        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));
        when(reviewRepository.findByUserIdAndId(
                user.getId(),
                userReview.getId())).thenReturn(Optional.empty());

        // Act + Assert
        assertThrows(ReviewConflictException.class, () -> {
            reviewService.deleteGameReview(authentication, userReview.getId());
        });

        // Verify
        verify(authentication).getName();
        verify(userRepository).findByEmail(user.getEmail());
        verify(reviewRepository).findByUserIdAndId(user.getId(), userReview.getId());
        verify(reviewRepository, never()).delete(any(Review.class));
    }

    @Test
    void deleteGameReview_WhenReviewExists_ShouldDeleteSuccessfully() {
        // Arrange
        when(authentication.getName()).thenReturn(user.getEmail());
        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));
        when(reviewRepository.findByUserIdAndId(
                user.getId(),
                userReview.getId())).thenReturn(Optional.of(userReview));
        when(reviewRepository.findAverageRatingByGameId(game.getId())).thenReturn(null);

        // Act
        reviewService.deleteGameReview(authentication, userReview.getId());

        // Assert
        assertEquals(0.0, game.getAverageRating());

        // Verify
        verify(authentication).getName();
        verify(userRepository).findByEmail(user.getEmail());
        verify(reviewRepository).findByUserIdAndId(user.getId(), userReview.getId());
        verify(reviewRepository).delete(userReview);
        verify(reviewRepository).flush();
        verify(reviewRepository).findAverageRatingByGameId(game.getId());
        verify(gameRepository).save(game);
    }
}
