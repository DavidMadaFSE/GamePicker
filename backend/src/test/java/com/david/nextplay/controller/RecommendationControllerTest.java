package com.david.nextplay.controller;

import static org.mockito.ArgumentMatchers.nullable;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.security.core.Authentication;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.david.nextplay.dto.recommendation.RecommendationResponse;
import com.david.nextplay.exception.UserConflictException;
import com.david.nextplay.service.CustomUserDetailsService;
import com.david.nextplay.service.JwtService;
import com.david.nextplay.service.RecommendationService;

@WebMvcTest(RecommendationController.class)
@AutoConfigureMockMvc(addFilters = false)
public class RecommendationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private RecommendationService recommendationService;

    @MockitoBean
    private JwtService jwtService;

    @MockitoBean
    private CustomUserDetailsService customUserDetailsService;

    private RecommendationResponse recommendationResponse;
    private RecommendationResponse secondRecommendationResponse;

    @BeforeEach
    void setUp() {
        recommendationResponse = new RecommendationResponse(
                1L,
                "Minecraft",
                "http://minecraft.jpg",
                5.0,
                3,
                "Recommended because it matches genres and platforms in your library.");

        secondRecommendationResponse = new RecommendationResponse(
                2L,
                "Halo",
                "http://halo.jpg",
                4.5,
                1,
                "Recommended because it matches platforms in your library.");
    }

    @Test
    void getRecommendationsForUser_WhenCalled_ShouldReturnRecommendationResponses() throws Exception {
        // Arrange
        when(recommendationService.getRecommendationsForUser(nullable(Authentication.class)))
                .thenReturn(List.of(recommendationResponse, secondRecommendationResponse));

        // Act + Assert
        mockMvc.perform(get("/api/recommendations"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].gameId").value(recommendationResponse.getGameId()))
                .andExpect(jsonPath("$[0].title").value(recommendationResponse.getTitle()))
                .andExpect(jsonPath("$[0].score").value(recommendationResponse.getScore()))
                .andExpect(jsonPath("$[1].gameId").value(secondRecommendationResponse.getGameId()));

        // Verify
        verify(recommendationService).getRecommendationsForUser(nullable(Authentication.class));
    }

    @Test
    void getRecommendationsForUser_WhenUserConflictOccurs_ShouldReturnConflict() throws Exception {
        // Arrange
        when(recommendationService.getRecommendationsForUser(nullable(Authentication.class)))
                .thenThrow(new UserConflictException("User not found"));

        // Act + Assert
        mockMvc.perform(get("/api/recommendations"))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message").value("User not found"))
                .andExpect(jsonPath("$.status").value(409));

        // Verify
        verify(recommendationService).getRecommendationsForUser(nullable(Authentication.class));
    }
}
