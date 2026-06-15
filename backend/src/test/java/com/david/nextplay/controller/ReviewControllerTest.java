package com.david.nextplay.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.nullable;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.david.nextplay.dto.review.CreateReviewRequest;
import com.david.nextplay.dto.review.ReviewResponse;
import com.david.nextplay.dto.review.UpdateReviewRequest;
import com.david.nextplay.exception.ReviewConflictException;
import com.david.nextplay.service.CustomUserDetailsService;
import com.david.nextplay.service.JwtService;
import com.david.nextplay.service.ReviewService;

@WebMvcTest(ReviewController.class)
@AutoConfigureMockMvc(addFilters = false)
public class ReviewControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ReviewService reviewService;

    @MockitoBean
    private JwtService jwtService;

    @MockitoBean
    private CustomUserDetailsService customUserDetailsService;

    private ReviewResponse reviewResponse;
    private ReviewResponse secondReviewResponse;
    private String createReviewJson;
    private String updateReviewJson;

    @BeforeEach
    void setUp() {
        reviewResponse = new ReviewResponse(
                1L,
                1L,
                "Minecraft",
                1L,
                "davidmada1",
                5,
                "Great game.",
                LocalDateTime.of(2026, 6, 12, 12, 0),
                LocalDateTime.of(2026, 6, 12, 12, 0));

        secondReviewResponse = new ReviewResponse(
                2L,
                1L,
                "Minecraft",
                2L,
                "admin",
                4,
                "Fun game.",
                LocalDateTime.of(2026, 6, 13, 12, 0),
                LocalDateTime.of(2026, 6, 13, 12, 0));

        createReviewJson = """
                {
                    "rating": 5,
                    "comment": "Great game."
                }
                """;

        updateReviewJson = """
                {
                    "rating": 4,
                    "comment": "Updated review."
                }
                """;
    }

    @Test
    void getReviewsForGame_WhenCalled_ShouldReturnPageReviewResponse() throws Exception {
        // Arrange
        Page<ReviewResponse> reviewPage = new PageImpl<>(List.of(reviewResponse, secondReviewResponse));
        when(reviewService.getReviewsForGame(
                eq(reviewResponse.getGameId()),
                eq(0),
                eq(10),
                eq("updatedAt"),
                eq("desc"))).thenReturn(reviewPage);

        // Act + Assert
        mockMvc.perform(get("/api/games/{gameId}/reviews", reviewResponse.getGameId())
                .param("page", "0")
                .param("size", "10")
                .param("sortBy", "updatedAt")
                .param("sortDir", "desc"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id").value(reviewResponse.getId()))
                .andExpect(jsonPath("$.content[0].gameId").value(reviewResponse.getGameId()))
                .andExpect(jsonPath("$.content[1].id").value(secondReviewResponse.getId()));

        // Verify
        verify(reviewService).getReviewsForGame(
                eq(reviewResponse.getGameId()),
                eq(0),
                eq(10),
                eq("updatedAt"),
                eq("desc"));
    }

    @Test
    void getMyGameReviews_WhenCalled_ShouldReturnPageReviewResponse() throws Exception {
        // Arrange
        Page<ReviewResponse> reviewPage = new PageImpl<>(List.of(reviewResponse));
        when(reviewService.getMyGameReviews(
                nullable(Authentication.class),
                eq(0),
                eq(10),
                eq("updatedAt"),
                eq("desc"))).thenReturn(reviewPage);

        // Act + Assert
        mockMvc.perform(get("/api/users/me/reviews")
                .param("page", "0")
                .param("size", "10")
                .param("sortBy", "updatedAt")
                .param("sortDir", "desc"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id").value(reviewResponse.getId()))
                .andExpect(jsonPath("$.content[0].username").value(reviewResponse.getUsername()));

        // Verify
        verify(reviewService).getMyGameReviews(
                nullable(Authentication.class),
                eq(0),
                eq(10),
                eq("updatedAt"),
                eq("desc"));
    }

    @Test
    void createGameReview_WhenRequestIsValid_ShouldReturnCreatedReviewResponse() throws Exception {
        // Arrange
        when(reviewService.createGameReview(
                nullable(Authentication.class),
                eq(reviewResponse.getGameId()),
                any(CreateReviewRequest.class))).thenReturn(reviewResponse);

        // Act + Assert
        mockMvc.perform(post("/api/games/{gameId}/reviews", reviewResponse.getGameId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(createReviewJson))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(reviewResponse.getId()))
                .andExpect(jsonPath("$.rating").value(reviewResponse.getRating()))
                .andExpect(jsonPath("$.comment").value(reviewResponse.getComment()));

        // Verify
        verify(reviewService).createGameReview(
                nullable(Authentication.class),
                eq(reviewResponse.getGameId()),
                any(CreateReviewRequest.class));
    }

    @Test
    void createGameReview_WhenRequestIsInvalid_ShouldReturnBadRequest() throws Exception {
        // Arrange
        String invalidJson = """
                {
                    "rating": 5,
                    "comment": ""
                }
                """;

        // Act + Assert
        mockMvc.perform(post("/api/games/{gameId}/reviews", reviewResponse.getGameId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(invalidJson))
                .andExpect(status().isBadRequest());

        // Verify
        verify(reviewService, never()).createGameReview(
                nullable(Authentication.class),
                eq(reviewResponse.getGameId()),
                any(CreateReviewRequest.class));
    }

    @Test
    void createGameReview_WhenReviewConflictOccurs_ShouldReturnConflict() throws Exception {
        // Arrange
        when(reviewService.createGameReview(
                nullable(Authentication.class),
                eq(reviewResponse.getGameId()),
                any(CreateReviewRequest.class)))
                .thenThrow(new ReviewConflictException("Review already exists."));

        // Act + Assert
        mockMvc.perform(post("/api/games/{gameId}/reviews", reviewResponse.getGameId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(createReviewJson))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message").value("Review already exists."))
                .andExpect(jsonPath("$.status").value(409));

        // Verify
        verify(reviewService).createGameReview(
                nullable(Authentication.class),
                eq(reviewResponse.getGameId()),
                any(CreateReviewRequest.class));
    }

    @Test
    void updateGameReview_WhenRequestIsValid_ShouldReturnReviewResponse() throws Exception {
        // Arrange
        reviewResponse.setRating(4);
        reviewResponse.setComment("Updated review.");
        when(reviewService.updateGameReview(
                nullable(Authentication.class),
                eq(reviewResponse.getId()),
                any(UpdateReviewRequest.class))).thenReturn(reviewResponse);

        // Act + Assert
        mockMvc.perform(put("/api/reviews/{id}", reviewResponse.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(updateReviewJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(reviewResponse.getId()))
                .andExpect(jsonPath("$.rating").value(4))
                .andExpect(jsonPath("$.comment").value("Updated review."));

        // Verify
        verify(reviewService).updateGameReview(
                nullable(Authentication.class),
                eq(reviewResponse.getId()),
                any(UpdateReviewRequest.class));
    }

    @Test
    void deleteGameReview_WhenReviewExists_ShouldReturnNoContent() throws Exception {
        // Arrange
        doNothing().when(reviewService).deleteGameReview(nullable(Authentication.class), eq(reviewResponse.getId()));

        // Act + Assert
        mockMvc.perform(delete("/api/reviews/{id}", reviewResponse.getId()))
                .andExpect(status().isNoContent());

        // Verify
        verify(reviewService).deleteGameReview(nullable(Authentication.class), eq(reviewResponse.getId()));
    }
}
