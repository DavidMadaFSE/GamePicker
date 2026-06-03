package com.david.nextplay.dto.game;

import java.time.LocalDate;
import java.util.List;

import com.david.nextplay.enums.Genre;
import com.david.nextplay.enums.Platform;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateGameRequest {
    
    @NotBlank
    private String title;

    @NotBlank
    private String description;

    @NotNull
    private LocalDate releaseDate;

    private String coverImageUrl;

    @NotEmpty
    private List<Genre> genres;

    @NotEmpty
    private List<Platform> platforms;
}
