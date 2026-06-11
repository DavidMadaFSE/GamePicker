package com.david.nextplay.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.david.nextplay.dto.recommendation.RecommendationResponse;
import com.david.nextplay.service.RecommendationService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/recommendations")
public class RecommendationController {
    
    private final RecommendationService recommendationService;

    @GetMapping
    public ResponseEntity<List<RecommendationResponse>> getRecommendationsForUser(Authentication authentication) {
        List<RecommendationResponse> response = recommendationService.getRecommendationsForUser(authentication);

        return ResponseEntity.ok(response);
    }
}
