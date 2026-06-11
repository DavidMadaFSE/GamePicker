package com.david.nextplay.entity;

import java.time.LocalDate;
import java.util.List;

import com.david.nextplay.enums.Genre;
import com.david.nextplay.enums.Platform;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "games")
public class Game {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String description;

    @Column(nullable = false)
    private LocalDate releaseDate;

    private String coverImageUrl;

    private Double averageRating;

    @ElementCollection
    @Enumerated(EnumType.STRING)
    @CollectionTable(
        name = "game_genres",
        joinColumns = @JoinColumn(name = "game_id")
    )
    @Column(name = "genre")
    private List<Genre> genres;

    @ElementCollection
    @Enumerated(EnumType.STRING)
    @CollectionTable(
        name = "game_platforms",
        joinColumns = @JoinColumn(name = "game_id")
    )
    @Column(name = "platform")
    private List<Platform> platforms;


}
