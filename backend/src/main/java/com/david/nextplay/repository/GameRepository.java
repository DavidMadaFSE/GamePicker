package com.david.nextplay.repository;

import com.david.nextplay.entity.Game;
import com.david.nextplay.enums.Genre;
import com.david.nextplay.enums.Platform;

import java.time.LocalDate;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface GameRepository extends JpaRepository<Game, Long> {

    boolean existsByTitleIgnoreCaseAndReleaseDate(String title, LocalDate releaseDate);

    @Query("""
            SELECT DISTINCT g FROM Game g
            LEFT JOIN g.genres gen
            LEFT JOIN g.platforms plat
            WHERE LOWER(g.title) LIKE CONCAT('%', :title, '%')
            AND (:releaseDate IS NULL OR g.releaseDate = :releaseDate)
            AND (:genre IS NULL OR gen = :genre)
            AND (:platform IS NULL OR plat = :platform)
            """)
    Page<Game> searchGames(
            @Param("title") String title,
            @Param("releaseDate") LocalDate releaseDate,
            @Param("genre") Genre genre,
            @Param("platform") Platform platform,
            Pageable pageable);
}
