package com.david.nextplay.dto.game;

import java.time.LocalDate;
import java.util.List;

import com.david.nextplay.enums.Genre;
import com.david.nextplay.enums.Platform;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class GameResponse {
    
    private Long id;
    private String title;
    private String description;
    private LocalDate releaseDate;
    private String coverImageUrl;
    private Double averageRating;
    private List<Genre> genres;
    private List<Platform> platforms;
}
