package com.david.nextplay.service;

import com.david.nextplay.dto.game.CreateGameRequest;
import com.david.nextplay.dto.game.GameResponse;
import com.david.nextplay.entity.Game;
import com.david.nextplay.enums.Genre;
import com.david.nextplay.enums.Platform;
import com.david.nextplay.exception.GameConflictException;
import com.david.nextplay.repository.GameRepository;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class GameService {

        private final GameRepository gameRepository;

        public Page<GameResponse> getGames(String title, LocalDate releaseDate, Genre genre, Platform platform,
                        int page, int size, String sortBy, String sortDir) {
                Sort sort = sortDir.equalsIgnoreCase("desc")
                                ? Sort.by(sortBy).descending()
                                : Sort.by(sortBy).ascending();

                Pageable pageable = PageRequest.of(page, size, sort);

                String normalizedTitle = title == null ? "" : title.trim().toLowerCase();

                Page<Game> games = gameRepository.searchGames(normalizedTitle, releaseDate, genre, platform, pageable);

                return games.map(this::mapToGameResponse);
        }

        public GameResponse getGameById(Long id) {
                Game game = gameRepository.findById(id)
                                .orElseThrow(() -> new EntityNotFoundException("Game with ID " + id + " not found."));

                return mapToGameResponse(game);
        }

        public GameResponse createGame(CreateGameRequest request) {
                if (gameRepository.existsByTitleIgnoreCaseAndReleaseDate(request.getTitle(),
                                request.getReleaseDate())) {
                        throw new GameConflictException(
                                        "Game with title '" + request.getTitle() + "' and release date "
                                                        + request.getReleaseDate() + " already exists.");
                }

                Game newGame = new Game();

                newGame.setTitle(request.getTitle());
                newGame.setDescription(request.getDescription());
                newGame.setReleaseDate(request.getReleaseDate());
                newGame.setCoverImageUrl(request.getCoverImageUrl());
                newGame.setGenres(request.getGenres());
                newGame.setPlatforms(request.getPlatforms());
                newGame.setAverageRating(0.0);

                Game savedGame = gameRepository.save(newGame);

                return mapToGameResponse(savedGame);
        }

        public List<GameResponse> createGames(List<CreateGameRequest> requests) {
                return requests.stream()
                                .map(this::createGame)
                                .toList();
        }

        public GameResponse updateGame(Long id, CreateGameRequest request) {
                Game game = gameRepository.findById(id)
                                .orElseThrow(() -> new EntityNotFoundException("Game with ID " + id + " not found."));

                game.setTitle(request.getTitle());
                game.setDescription(request.getDescription());
                game.setReleaseDate(request.getReleaseDate());
                game.setCoverImageUrl(request.getCoverImageUrl());
                game.setGenres(request.getGenres());
                game.setPlatforms(request.getPlatforms());

                gameRepository.save(game);

                return mapToGameResponse(game);
        }

        public void deleteGame(Long id) {
                Game game = gameRepository.findById(id)
                                .orElseThrow(() -> new EntityNotFoundException("Game with ID " + id + " not found."));

                gameRepository.delete(game);
        }

        public List<GameResponse> searchGames(String title, LocalDate releaseDate, Genre genre, Platform platform) {
                List<Game> games = gameRepository.findAll();

                return games.stream()
                                .filter(game -> title == null || title.isBlank()
                                                || game.getTitle().toLowerCase().contains(title.toLowerCase()))

                                .filter(game -> releaseDate == null
                                                || game.getReleaseDate().equals(releaseDate))

                                .filter(game -> genre == null
                                                || game.getGenres().contains(genre))

                                .filter(game -> platform == null
                                                || game.getPlatforms().contains(platform))

                                .map(this::mapToGameResponse)
                                .toList();
        }

        private GameResponse mapToGameResponse(Game game) {
                return new GameResponse(
                                game.getId(),
                                game.getTitle(),
                                game.getDescription(),
                                game.getReleaseDate(),
                                game.getCoverImageUrl(),
                                game.getAverageRating(),
                                game.getGenres(),
                                game.getPlatforms());
        }
}
