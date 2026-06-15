package com.david.nextplay.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;

import com.david.nextplay.dto.recommendation.RecommendationResponse;
import com.david.nextplay.entity.Game;
import com.david.nextplay.entity.LibraryEntry;
import com.david.nextplay.entity.User;
import com.david.nextplay.enums.GameStatus;
import com.david.nextplay.enums.Genre;
import com.david.nextplay.enums.Platform;
import com.david.nextplay.enums.Role;
import com.david.nextplay.exception.UserConflictException;
import com.david.nextplay.repository.GameRepository;
import com.david.nextplay.repository.LibraryEntryRepository;
import com.david.nextplay.repository.UserRepository;

@ExtendWith(MockitoExtension.class)
public class RecommendationServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private LibraryEntryRepository libraryEntryRepository;

    @Mock
    private GameRepository gameRepository;

    @Mock
    private Authentication authentication;

    @InjectMocks
    private RecommendationService recommendationService;

    private User user;
    private Game minecraft;
    private Game eldenRing;
    private Game stardewValley;
    private Game fifa;
    private Game halo;
    private LibraryEntry minecraftLibraryEntry;

    @BeforeEach
    void setUp() {
        user = new User();
        minecraft = new Game();
        eldenRing = new Game();
        stardewValley = new Game();
        fifa = new Game();
        halo = new Game();
        minecraftLibraryEntry = new LibraryEntry();

        user.setId(1L);
        user.setUsername("davidmada1");
        user.setEmail("david@example.com");
        user.setPassword("encodedPassword");
        user.setRole(Role.USER);
        user.setCreatedAt(LocalDateTime.of(2026, 6, 12, 12, 0));
        user.setUpdatedAt(LocalDateTime.of(2026, 6, 12, 12, 0));

        minecraft.setId(1L);
        minecraft.setTitle("Minecraft");
        minecraft.setDescription("A sandbox survival game.");
        minecraft.setCoverImageUrl("http://minecraft.jpg");
        minecraft.setReleaseDate(LocalDate.of(2011, 11, 17));
        minecraft.setAverageRating(5.0);
        minecraft.setPlatforms(List.of(Platform.PC, Platform.XBOX));
        minecraft.setGenres(List.of(Genre.ADVENTURE, Genre.SIMULATION));

        eldenRing.setId(2L);
        eldenRing.setTitle("Elden Ring");
        eldenRing.setDescription("An open-world action RPG.");
        eldenRing.setCoverImageUrl("http://eldenring.jpg");
        eldenRing.setReleaseDate(LocalDate.of(2022, 2, 25));
        eldenRing.setAverageRating(4.8);
        eldenRing.setPlatforms(List.of(Platform.PC, Platform.PLAYSTATION));
        eldenRing.setGenres(List.of(Genre.RPG, Genre.ACTION));

        stardewValley.setId(3L);
        stardewValley.setTitle("Stardew Valley");
        stardewValley.setDescription("A cozy farming simulation game.");
        stardewValley.setCoverImageUrl("http://stardew.jpg");
        stardewValley.setReleaseDate(LocalDate.of(2016, 2, 26));
        stardewValley.setAverageRating(4.7);
        stardewValley.setPlatforms(List.of(Platform.PC, Platform.NINTENDO_SWITCH));
        stardewValley.setGenres(List.of(Genre.SIMULATION));

        fifa.setId(4L);
        fifa.setTitle("FIFA");
        fifa.setDescription("A soccer sports game.");
        fifa.setCoverImageUrl("http://fifa.jpg");
        fifa.setReleaseDate(LocalDate.of(2023, 9, 29));
        fifa.setAverageRating(3.9);
        fifa.setPlatforms(List.of(Platform.PLAYSTATION));
        fifa.setGenres(List.of(Genre.SPORTS));

        halo.setId(5L);
        halo.setTitle("Halo");
        halo.setDescription("A sci-fi shooter.");
        halo.setCoverImageUrl("http://halo.jpg");
        halo.setReleaseDate(LocalDate.of(2001, 11, 15));
        halo.setAverageRating(4.5);
        halo.setPlatforms(List.of(Platform.XBOX));
        halo.setGenres(List.of(Genre.SHOOTER));

        minecraftLibraryEntry.setId(1L);
        minecraftLibraryEntry.setUser(user);
        minecraftLibraryEntry.setGame(minecraft);
        minecraftLibraryEntry.setGameStatus(GameStatus.COMPLETED);
        minecraftLibraryEntry.setCreatedAt(LocalDateTime.of(2026, 6, 11, 12, 0));
        minecraftLibraryEntry.setUpdatedAt(LocalDateTime.of(2026, 6, 11, 12, 0));
    }

    @Test
    void getRecommendationsForUser_WhenUserDoesNotExist_ShouldThrowUserConflictException() {
        // Arrange
        when(authentication.getName()).thenReturn(user.getEmail());
        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.empty());

        // Act + Assert
        assertThrows(UserConflictException.class, () -> {
            recommendationService.getRecommendationsForUser(authentication);
        });

        // Verify
        verify(authentication).getName();
        verify(userRepository).findByEmail(user.getEmail());
        verify(libraryEntryRepository, never()).findByUserId(user.getId());
        verify(gameRepository, never()).findAll();
    }

    @Test
    void getRecommendationsForUser_WhenLibraryIsEmpty_ShouldReturnFirstTenGamesWithStarterReason() {
        // Arrange
        List<Game> games = List.of(
                minecraft,
                eldenRing,
                stardewValley,
                fifa,
                halo,
                createGame(6L),
                createGame(7L),
                createGame(8L),
                createGame(9L),
                createGame(10L),
                createGame(11L));

        when(authentication.getName()).thenReturn(user.getEmail());
        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));
        when(libraryEntryRepository.findByUserId(user.getId())).thenReturn(List.of());
        when(gameRepository.findAll()).thenReturn(games);

        // Act
        List<RecommendationResponse> response = recommendationService.getRecommendationsForUser(authentication);

        // Assert
        assertEquals(10, response.size());
        assertEquals(minecraft.getId(), response.get(0).getGameId());
        assertEquals(createStarterReason(), response.get(0).getReason());
        assertEquals(0, response.get(0).getScore());
        assertFalse(response.stream().anyMatch(recommendation -> recommendation.getGameId().equals(11L)));

        // Verify
        verify(authentication).getName();
        verify(userRepository).findByEmail(user.getEmail());
        verify(libraryEntryRepository).findByUserId(user.getId());
        verify(gameRepository).findAll();
    }

    @Test
    void getRecommendationsForUser_WhenLibraryHasGames_ShouldExcludeOwnedGamesAndReturnScoredRecommendations() {
        // Arrange
        when(authentication.getName()).thenReturn(user.getEmail());
        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));
        when(libraryEntryRepository.findByUserId(user.getId())).thenReturn(List.of(minecraftLibraryEntry));
        when(gameRepository.findAll()).thenReturn(List.of(
                minecraft,
                eldenRing,
                stardewValley,
                fifa,
                halo));

        // Act
        List<RecommendationResponse> response = recommendationService.getRecommendationsForUser(authentication);

        // Assert
        assertEquals(3, response.size());
        assertEquals(stardewValley.getId(), response.get(0).getGameId());
        assertEquals(3, response.get(0).getScore());
        assertEquals("Recommended because it matches genres and platforms in your library.", response.get(0).getReason());
        assertEquals(eldenRing.getId(), response.get(1).getGameId());
        assertEquals(1, response.get(1).getScore());
        assertEquals("Recommended because it matches platforms in your library.", response.get(1).getReason());
        assertEquals(halo.getId(), response.get(2).getGameId());
        assertEquals(1, response.get(2).getScore());
        assertEquals("Recommended because it matches platforms in your library.", response.get(2).getReason());
        assertFalse(response.stream().anyMatch(recommendation -> recommendation.getGameId().equals(minecraft.getId())));
        assertFalse(response.stream().anyMatch(recommendation -> recommendation.getGameId().equals(fifa.getId())));

        // Verify
        verify(authentication).getName();
        verify(userRepository).findByEmail(user.getEmail());
        verify(libraryEntryRepository).findByUserId(user.getId());
        verify(gameRepository).findAll();
    }

    @Test
    void getRecommendationsForUser_WhenGameOnlyMatchesGenre_ShouldReturnGenreReason() {
        // Arrange
        stardewValley.setPlatforms(List.of(Platform.NINTENDO_SWITCH));

        when(authentication.getName()).thenReturn(user.getEmail());
        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));
        when(libraryEntryRepository.findByUserId(user.getId())).thenReturn(List.of(minecraftLibraryEntry));
        when(gameRepository.findAll()).thenReturn(List.of(minecraft, stardewValley));

        // Act
        List<RecommendationResponse> response = recommendationService.getRecommendationsForUser(authentication);

        // Assert
        assertEquals(1, response.size());
        assertEquals(stardewValley.getId(), response.get(0).getGameId());
        assertEquals(2, response.get(0).getScore());
        assertEquals("Recommended because it matches genres in your library.", response.get(0).getReason());

        // Verify
        verify(authentication).getName();
        verify(userRepository).findByEmail(user.getEmail());
        verify(libraryEntryRepository).findByUserId(user.getId());
        verify(gameRepository).findAll();
    }

    @Test
    void getRecommendationsForUser_WhenGamesHaveNullGenresOrPlatforms_ShouldIgnoreNullLists() {
        // Arrange
        stardewValley.setGenres(null);
        stardewValley.setPlatforms(null);

        when(authentication.getName()).thenReturn(user.getEmail());
        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));
        when(libraryEntryRepository.findByUserId(user.getId())).thenReturn(List.of(minecraftLibraryEntry));
        when(gameRepository.findAll()).thenReturn(List.of(minecraft, stardewValley, halo));

        // Act
        List<RecommendationResponse> response = recommendationService.getRecommendationsForUser(authentication);

        // Assert
        assertEquals(1, response.size());
        assertEquals(halo.getId(), response.get(0).getGameId());
        assertEquals(1, response.get(0).getScore());

        // Verify
        verify(authentication).getName();
        verify(userRepository).findByEmail(user.getEmail());
        verify(libraryEntryRepository).findByUserId(user.getId());
        verify(gameRepository).findAll();
    }

    private Game createGame(Long id) {
        Game game = new Game();
        game.setId(id);
        game.setTitle("Game " + id);
        game.setDescription("Description " + id);
        game.setCoverImageUrl("http://game" + id + ".jpg");
        game.setReleaseDate(LocalDate.of(2020, 1, 1));
        game.setAverageRating(4.0);
        game.setPlatforms(List.of(Platform.PC));
        game.setGenres(List.of(Genre.ACTION));
        return game;
    }

    private String createStarterReason() {
        return "Recommended to help you start building your library.";
    }
}
