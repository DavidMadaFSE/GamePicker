package com.david.nextplay.service;

import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import com.david.nextplay.dto.recommendation.RecommendationResponse;
import com.david.nextplay.entity.Game;
import com.david.nextplay.entity.LibraryEntry;
import com.david.nextplay.entity.User;
import com.david.nextplay.enums.Genre;
import com.david.nextplay.enums.Platform;
import com.david.nextplay.exception.UserConflictException;
import com.david.nextplay.repository.GameRepository;
import com.david.nextplay.repository.LibraryEntryRepository;
import com.david.nextplay.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RecommendationService {

    private final UserRepository userRepository;
    private final LibraryEntryRepository libraryEntryRepository;
    private final GameRepository gameRepository;

    public List<RecommendationResponse> getRecommendationsForUser(Authentication authentication) {
        String email = authentication.getName();

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserConflictException("User not found"));

        List<LibraryEntry> library = libraryEntryRepository.findByUserId(user.getId());

        if (library.isEmpty()) {
            return gameRepository.findAll()
                    .stream()
                    .limit(10)
                    .map(game -> mapToResponse(
                            game,
                            0,
                            "Recommended to help you start building your library."
                    ))
                    .toList();
        }

        Set<Long> ownedGameIds = library.stream()
                .map(entry -> entry.getGame().getId())
                .collect(Collectors.toSet());

        Set<Genre> genres = library.stream()
                .map(entry -> entry.getGame().getGenres())
                .filter(Objects::nonNull)
                .flatMap(Collection::stream)
                .collect(Collectors.toSet());

        Set<Platform> platforms = library.stream()
                .map(entry -> entry.getGame().getPlatforms())
                .filter(Objects::nonNull)
                .flatMap(Collection::stream)
                .collect(Collectors.toSet());

        return gameRepository.findAll()
                .stream()
                .filter(game -> !ownedGameIds.contains(game.getId()))
                .filter(game ->
                        hasMatchingGenre(game, genres) ||
                        hasMatchingPlatform(game, platforms)
                )
                .map(game -> {
                    int score = getRecommendationScore(game, genres, platforms);
                    String reason = buildReason(game, genres, platforms);
                    return mapToResponse(game, score, reason);
                })
                .sorted((r1, r2) -> Integer.compare(r2.getScore(), r1.getScore()))
                .limit(10)
                .toList();
    }

    private int getRecommendationScore(Game game, Set<Genre> genres, Set<Platform> platforms) {
        int score = 0;

        if (hasMatchingGenre(game, genres)) {
            score += 2;
        }

        if (hasMatchingPlatform(game, platforms)) {
            score += 1;
        }

        return score;
    }

    private boolean hasMatchingGenre(Game game, Set<Genre> genres) {
        return game.getGenres() != null &&
                game.getGenres()
                        .stream()
                        .anyMatch(genres::contains);
    }

    private boolean hasMatchingPlatform(Game game, Set<Platform> platforms) {
        return game.getPlatforms() != null &&
                game.getPlatforms()
                        .stream()
                        .anyMatch(platforms::contains);
    }

    private String buildReason(Game game, Set<Genre> genres, Set<Platform> platforms) {
        boolean genreMatch = hasMatchingGenre(game, genres);
        boolean platformMatch = hasMatchingPlatform(game, platforms);

        if (genreMatch && platformMatch) {
            return "Recommended because it matches genres and platforms in your library.";
        }

        if (genreMatch) {
            return "Recommended because it matches genres in your library.";
        }

        if (platformMatch) {
            return "Recommended because it matches platforms in your library.";
        }

        return "Recommended based on your library.";
    }

    private RecommendationResponse mapToResponse(Game game, int score, String reason) {
        return new RecommendationResponse(
                game.getId(),
                game.getTitle(),
                game.getCoverImageUrl(),
                game.getAverageRating(),
                score,
                reason
        );
    }
}