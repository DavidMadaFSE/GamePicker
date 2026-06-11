package com.david.nextplay.dto.review;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateReviewRequest {
    
    @NotNull
    @Min(0)
    @Max(5)
    private Integer rating;

    @NotBlank
    private String comment;
}
