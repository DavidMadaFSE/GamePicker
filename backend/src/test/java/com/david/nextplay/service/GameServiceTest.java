package com.david.nextplay.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import com.david.nextplay.dto.game.CreateGameRequest;
import com.david.nextplay.dto.game.GameResponse;
import com.david.nextplay.entity.Game;
import com.david.nextplay.enums.Genre;
import com.david.nextplay.enums.Platform;
import com.david.nextplay.exception.GameConflictException;
import com.david.nextplay.repository.GameRepository;

import jakarta.persistence.EntityNotFoundException;

@ExtendWith(MockitoExtension.class)
public class GameServiceTest {

    @Mock
    private GameRepository gameRepository;

    @InjectMocks
    private GameService gameService;

    private Game game;
    private Game secondGame;
    private Page<Game> gamePage;
    private CreateGameRequest createGameRequest1;
    private CreateGameRequest createGameRequest2;

    @BeforeEach
    void setUp() {
        game = new Game();
        gamePage = new PageImpl<>(List.of(game));
        createGameRequest1 = new CreateGameRequest();
        createGameRequest2 = new CreateGameRequest();

        game.setId(1L);
        game.setTitle("   Minecraft   ");
        game.setDescription(
                "A sandbox survival game where players build, explore, and craft in block-based worlds.");
        game.setCoverImageUrl("http://minecraft.jpg");
        game.setReleaseDate(LocalDate.of(2011, 11, 17));
        game.setAverageRating(5.0);
        game.setPlatforms(
                List.of(Platform.PC, Platform.PLAYSTATION, Platform.XBOX, Platform.NINTENDO_SWITCH, Platform.MOBILE));
        game.setGenres(List.of(Genre.ADVENTURE, Genre.SIMULATION));

        secondGame = new Game();
        secondGame.setId(2L);
        secondGame.setTitle("Elden Ring");
        secondGame.setDescription("An open-world action RPG.");
        secondGame.setCoverImageUrl("http://eldenring.jpg");
        secondGame.setReleaseDate(LocalDate.of(2022, 2, 25));
        secondGame.setAverageRating(4.8);
        secondGame.setPlatforms(List.of(Platform.PC, Platform.PLAYSTATION));
        secondGame.setGenres(List.of(Genre.RPG, Genre.ACTION));

        createGameRequest1.setTitle("Fortnite");
        createGameRequest1.setDescription("Battle royale");
        createGameRequest1.setReleaseDate(LocalDate.of(2017, 07, 25));
        createGameRequest1.setCoverImageUrl("http://fortnite.jpg");
        createGameRequest1.setPlatforms(
                List.of(Platform.PC, Platform.PLAYSTATION, Platform.XBOX, Platform.NINTENDO_SWITCH, Platform.MOBILE));
        createGameRequest1.setGenres(List.of(Genre.ACTION, Genre.SHOOTER));

        createGameRequest2 = new CreateGameRequest();
        createGameRequest2.setTitle("Rocket League");
        createGameRequest2.setDescription("Soccer with cars");
        createGameRequest2.setReleaseDate(LocalDate.of(2015, 7, 7));
        createGameRequest2.setCoverImageUrl("http://rocketleague.jpg");
        createGameRequest2.setPlatforms(List.of(Platform.PC, Platform.PLAYSTATION));
        createGameRequest2.setGenres(List.of(Genre.ACTION, Genre.SHOOTER));
    }

    @Test
    void getGames_WhenFilterProvided_ShouldReturnPageGameResponse() {
        // Arrange
        when(gameRepository.searchGames(
                eq("minecraft"),
                eq(game.getReleaseDate()),
                eq(game.getGenres().get(0)),
                eq(game.getPlatforms().get(0)),
                any(Pageable.class))).thenReturn(gamePage);

        // Act
        Page<GameResponse> response = gameService.getGames(game.getTitle(),
                game.getReleaseDate(),
                game.getGenres().get(0),
                game.getPlatforms().get(0),
                0,
                10,
                "title",
                "DESC");

        // Assert
        assertNotNull(response);
        assertEquals(1, response.getContent().size());
        assertEquals(game.getId(), response.getContent().get(0).getId());
        assertEquals(game.getTitle(), response.getContent().get(0).getTitle());
        assertEquals(game.getReleaseDate(), response.getContent().get(0).getReleaseDate());
        assertEquals(game.getAverageRating(), response.getContent().get(0).getAverageRating());

        // Verify
        verify(gameRepository).searchGames(
                eq("minecraft"),
                eq(game.getReleaseDate()),
                eq(game.getGenres().get(0)),
                eq(game.getPlatforms().get(0)),
                any(Pageable.class));
    }

    @Test
    void getGames_WhenFilterIsNotProvided_ShouldReturnPageGameResponse() {
        // Arrange
        when(gameRepository.searchGames(
                any(),
                any(),
                any(),
                any(),
                any(Pageable.class))).thenReturn(gamePage);

        // Act
        Page<GameResponse> response = gameService.getGames(null,
                null,
                null,
                null,
                0,
                10,
                "title",
                "asc");

        // Assert
        assertNotNull(response);
        assertEquals(1, response.getContent().size());
        assertEquals(game.getId(), response.getContent().get(0).getId());
        assertEquals(game.getTitle(), response.getContent().get(0).getTitle());
        assertEquals(game.getReleaseDate(), response.getContent().get(0).getReleaseDate());
        assertEquals(game.getAverageRating(), response.getContent().get(0).getAverageRating());

        // Verify
        verify(gameRepository).searchGames(
                eq(""),
                eq(null),
                eq(null),
                eq(null),
                any(Pageable.class));
    }

    @Test
    void getGameById_WhenGameIdDoesNotExist_ShouldThrowEntityNotFoundException() {
        // Arrange
        when(gameRepository.findById(game.getId())).thenReturn(Optional.empty());

        // Act + Assert
        assertThrows(EntityNotFoundException.class, () -> {
            gameService.getGameById(game.getId());
        });

        // Verify
        verify(gameRepository).findById(game.getId());
    }

    @Test
    void getGameById_WhenGameIdExists_ShouldReturnGameResponse() {
        // Arrange
        when(gameRepository.findById(game.getId())).thenReturn(Optional.of(game));

        // Act
        GameResponse response = gameService.getGameById(game.getId());

        // Assert
        assertNotNull(response);
        assertEquals(game.getId(), response.getId());
        assertEquals(game.getTitle(), response.getTitle());
        assertEquals(game.getAverageRating(), response.getAverageRating());

        // Verify
        verify(gameRepository).findById(game.getId());
    }

    @Test
    void createGame_WhenGameTitleAndReleaseDateExists_ShouldThrowGameConflictException() {
        // Arrange
        when(gameRepository.existsByTitleIgnoreCaseAndReleaseDate(
                createGameRequest1.getTitle(),
                createGameRequest1.getReleaseDate())).thenReturn(true);

        // Act + Assert
        assertThrows(GameConflictException.class, () -> {
            gameService.createGame(createGameRequest1);
        });

        // Verify
        verify(gameRepository).existsByTitleIgnoreCaseAndReleaseDate(
                createGameRequest1.getTitle(),
                createGameRequest1.getReleaseDate());
        verify(gameRepository, never()).save(any(Game.class));

    }

    @Test
    void createGame_WhenGameTitleAndReleaseDateDoesNotExist_ShouldReturnGameResponse() {
        // Arrange
        when(gameRepository.existsByTitleIgnoreCaseAndReleaseDate(
                createGameRequest1.getTitle(),
                createGameRequest1.getReleaseDate())).thenReturn(false);

        Game newGame = new Game();
        newGame.setId(2L);
        newGame.setTitle(createGameRequest1.getTitle());
        newGame.setDescription(createGameRequest1.getDescription());
        newGame.setCoverImageUrl(createGameRequest1.getCoverImageUrl());
        newGame.setReleaseDate(createGameRequest1.getReleaseDate());
        newGame.setAverageRating(0.0);
        newGame.setPlatforms(createGameRequest1.getPlatforms());
        newGame.setGenres(createGameRequest1.getGenres());

        when(gameRepository.save(any(Game.class))).thenReturn(newGame);

        // Act
        GameResponse response = gameService.createGame(createGameRequest1);

        // Assert
        assertNotNull(response);
        assertEquals(createGameRequest1.getTitle(), response.getTitle());
        assertEquals(createGameRequest1.getReleaseDate(), response.getReleaseDate());
        assertEquals(2L, response.getId());

        // Verify
        verify(gameRepository).existsByTitleIgnoreCaseAndReleaseDate(
                createGameRequest1.getTitle(),
                createGameRequest1.getReleaseDate());
        verify(gameRepository).save(any(Game.class));
    }

    @Test
    void createGames_WhenValidRequests_ShouldReturnListOfGameResponses() {
        // Arrange
        when(gameRepository.existsByTitleIgnoreCaseAndReleaseDate(
                createGameRequest1.getTitle(),
                createGameRequest1.getReleaseDate())).thenReturn(false);
        when(gameRepository.existsByTitleIgnoreCaseAndReleaseDate(
                createGameRequest2.getTitle(),
                createGameRequest2.getReleaseDate())).thenReturn(false);

        Game firstSavedGame = new Game();
        firstSavedGame.setId(2L);
        firstSavedGame.setTitle(createGameRequest1.getTitle());
        firstSavedGame.setDescription(createGameRequest1.getDescription());
        firstSavedGame.setCoverImageUrl(createGameRequest1.getCoverImageUrl());
        firstSavedGame.setReleaseDate(createGameRequest1.getReleaseDate());
        firstSavedGame.setAverageRating(0.0);
        firstSavedGame.setPlatforms(createGameRequest1.getPlatforms());
        firstSavedGame.setGenres(createGameRequest1.getGenres());

        Game secondSavedGame = new Game();
        secondSavedGame.setId(3L);
        secondSavedGame.setTitle(createGameRequest2.getTitle());
        secondSavedGame.setDescription(createGameRequest2.getDescription());
        secondSavedGame.setCoverImageUrl(createGameRequest2.getCoverImageUrl());
        secondSavedGame.setReleaseDate(createGameRequest2.getReleaseDate());
        secondSavedGame.setAverageRating(0.0);
        secondSavedGame.setPlatforms(createGameRequest2.getPlatforms());
        secondSavedGame.setGenres(createGameRequest2.getGenres());

        when(gameRepository.save(any(Game.class))).thenReturn(firstSavedGame, secondSavedGame);

        // Act
        var responses = gameService.createGames(List.of(createGameRequest1, createGameRequest2));

        // Assert
        assertNotNull(responses);
        assertEquals(2, responses.size());
        assertEquals(firstSavedGame.getId(), responses.get(0).getId());
        assertEquals(firstSavedGame.getTitle(), responses.get(0).getTitle());
        assertEquals(secondSavedGame.getId(), responses.get(1).getId());
        assertEquals(secondSavedGame.getTitle(), responses.get(1).getTitle());

        // Verify
        verify(gameRepository).existsByTitleIgnoreCaseAndReleaseDate(
                createGameRequest1.getTitle(),
                createGameRequest1.getReleaseDate());
        verify(gameRepository).existsByTitleIgnoreCaseAndReleaseDate(
                createGameRequest2.getTitle(),
                createGameRequest2.getReleaseDate());
        verify(gameRepository, times(2)).save(any(Game.class));
    }

    @Test
    void updateGame_WhenGameIdDoesNotExist_ShouldThrowEntityNotFoundException() {
        // Arrange
        when(gameRepository.findById(game.getId())).thenReturn(Optional.empty());

        // Act + assert
        assertThrows(EntityNotFoundException.class, () -> {
            gameService.updateGame(game.getId(), createGameRequest1);
        });

        // Verify
        verify(gameRepository).findById(game.getId());
        verify(gameRepository, never()).save(any(Game.class));
    }

    @Test
    void updateGame_WhenGameIdExists_ShouldReturnGameResponse() {
        // Arrange
        when(gameRepository.findById(game.getId())).thenReturn(Optional.of(game));
        // Prepare the object that repository.save will return (separate instance)
        Game savedGame = new Game();
        savedGame.setId(game.getId());
        savedGame.setTitle(createGameRequest1.getTitle());
        savedGame.setDescription(createGameRequest1.getDescription());
        savedGame.setReleaseDate(createGameRequest1.getReleaseDate());
        savedGame.setCoverImageUrl(createGameRequest1.getCoverImageUrl());
        savedGame.setGenres(createGameRequest1.getGenres());
        savedGame.setPlatforms(createGameRequest1.getPlatforms());
        savedGame.setAverageRating(game.getAverageRating());

        when(gameRepository.save(any(Game.class))).thenReturn(savedGame);

        // Act
        GameResponse response = gameService.updateGame(game.getId(), createGameRequest1);

        // Assert
        assertNotNull(response);
        assertEquals(game.getId(), response.getId());
        assertEquals(createGameRequest1.getTitle(), response.getTitle());
        assertEquals(createGameRequest1.getDescription(), response.getDescription());
        assertEquals(createGameRequest1.getReleaseDate(), response.getReleaseDate());

        // Verify
        verify(gameRepository).findById(game.getId());
        verify(gameRepository).save(any(Game.class));
    }

    @Test
    void deleteGame_WhenGameIdDoesNotExist_ShouldThrowEntityNotFoundException() {
        // Arrange
        when(gameRepository.findById(game.getId())).thenReturn(Optional.empty());

        // Act + Assert
        assertThrows(EntityNotFoundException.class, () -> {
            gameService.deleteGame(game.getId());
        });

        // Verify
        verify(gameRepository).findById(game.getId());
        verify(gameRepository, never()).delete(any(Game.class));
    }

    @Test
    void deleteGame_WhenGameIdExists_ShouldDeleteSuccessfully() {
        // Arrange
        when(gameRepository.findById(game.getId())).thenReturn(Optional.of(game));

        // Act
        gameService.deleteGame(game.getId());

        // Verify
        verify(gameRepository).findById(game.getId());
        verify(gameRepository).delete(game);
    }

    @Test
    void searchGames_WhenNoFiltersProvided_ShouldReturnAllGames() {
        // Arrange
        when(gameRepository.findAll()).thenReturn(List.of(game, secondGame));

        // Act
        List<GameResponse> response = gameService.searchGames(null, null, null, null);

        // Assert
        assertEquals(2, response.size());
        assertEquals(game.getTitle(), response.get(0).getTitle());
        assertEquals(secondGame.getTitle(), response.get(1).getTitle());

        // Verify
        verify(gameRepository).findAll();
    }

    @Test
    void searchGames_WhenTitleProvided_ShouldReturnMatchingGames() {
        // Arrange
        when(gameRepository.findAll()).thenReturn(List.of(game, secondGame));

        // Act
        List<GameResponse> response = gameService.searchGames("mine", null, null, null);

        // Assert
        assertEquals(1, response.size());
        assertEquals(game.getTitle(), response.get(0).getTitle());

        // Verify
        verify(gameRepository).findAll();
    }

    @Test
    void searchGames_WhenAllFiltersProvided_ShouldReturnMatchingGames() {
        // Arrange
        when(gameRepository.findAll()).thenReturn(List.of(game, secondGame));

        // Act
        List<GameResponse> response = gameService.searchGames(
                "elden",
                LocalDate.of(2022, 2, 25),
                Genre.RPG,
                Platform.PC);

        // Assert
        assertEquals(1, response.size());
        assertEquals("Elden Ring", response.get(0).getTitle());

        // Verify
        verify(gameRepository).findAll();
    }

}
