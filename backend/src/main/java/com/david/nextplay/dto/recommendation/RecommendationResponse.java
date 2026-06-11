package com.david.nextplay.dto.recommendation;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class RecommendationResponse {
    
    private Long gameId;
    private String title;
    private String coverImageUrl;
    private Double averageRating;
    private Integer score;
    private String reason;
}
