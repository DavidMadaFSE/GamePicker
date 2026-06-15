package com.david.nextplay.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;

import com.david.nextplay.dto.library.AddLibraryEntryRequest;
import com.david.nextplay.dto.library.LibraryEntryResponse;
import com.david.nextplay.entity.Game;
import com.david.nextplay.entity.LibraryEntry;
import com.david.nextplay.entity.User;
import com.david.nextplay.enums.GameStatus;
import com.david.nextplay.enums.Genre;
import com.david.nextplay.enums.Platform;
import com.david.nextplay.enums.Role;
import com.david.nextplay.exception.GameConflictException;
import com.david.nextplay.exception.LibraryConflictException;
import com.david.nextplay.exception.UserConflictException;
import com.david.nextplay.repository.GameRepository;
import com.david.nextplay.repository.LibraryEntryRepository;
import com.david.nextplay.repository.UserRepository;

@ExtendWith(MockitoExtension.class)
public class LibraryServiceTest {

    @Mock
    private LibraryEntryRepository libraryEntryRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private GameRepository gameRepository;

    @Mock
    private Authentication authentication;

    @InjectMocks
    private LibraryService libraryService;

    private LibraryEntry libraryEntry;
    private LibraryEntry secondLibraryEntry;
    private Game game;
    private Game secondGame;
    private User user;
    private Page<LibraryEntry> libraryPageSingleEntry;
    private Page<LibraryEntry> libraryPageMultipleEntries;
    private AddLibraryEntryRequest addLibraryEntryRequest;

    @BeforeEach
    void setUp() {
        libraryEntry = new LibraryEntry();
        secondLibraryEntry = new LibraryEntry();
        game = new Game();
        secondGame = new Game();
        user = new User();
        libraryPageSingleEntry = new PageImpl<>(List.of(libraryEntry));
        libraryPageMultipleEntries = new PageImpl<>(List.of(libraryEntry, secondLibraryEntry));
        addLibraryEntryRequest = new AddLibraryEntryRequest();

        addLibraryEntryRequest.setGameId(1L);

        user.setId(1L);
        user.setUsername("davidmada1");
        user.setEmail("david@example.com");
        user.setPassword("encodedPassword");
        user.setRole(Role.USER);
        user.setCreatedAt(LocalDateTime.of(2026, 06, 11, 12, 00));
        user.setUpdatedAt(LocalDateTime.of(2026, 06, 11, 12, 00));

        game.setId(1L);
        game.setTitle("Minecraft");
        game.setDescription(
                "A sandbox survival game where players build, explore, and craft in block-based worlds.");
        game.setCoverImageUrl("http://minecraft.jpg");
        game.setReleaseDate(LocalDate.of(2011, 11, 17));
        game.setAverageRating(5.0);
        game.setPlatforms(
                List.of(Platform.PC, Platform.PLAYSTATION, Platform.XBOX, Platform.NINTENDO_SWITCH, Platform.MOBILE));
        game.setGenres(List.of(Genre.ADVENTURE, Genre.SIMULATION));

        secondGame.setId(2L);
        secondGame.setTitle("Elden Ring");
        secondGame.setDescription("An open-world action RPG.");
        secondGame.setCoverImageUrl("http://eldenring.jpg");
        secondGame.setReleaseDate(LocalDate.of(2022, 2, 25));
        secondGame.setAverageRating(4.8);
        secondGame.setPlatforms(List.of(Platform.PC, Platform.PLAYSTATION));
        secondGame.setGenres(List.of(Genre.RPG, Genre.ACTION));

        libraryEntry.setId(1L);
        libraryEntry.setGame(game);
        libraryEntry.setUser(user);
        libraryEntry.setGameStatus(GameStatus.WANT_TO_PLAY);
        libraryEntry.setCreatedAt(LocalDateTime.of(2026, 06, 11, 12, 00));
        libraryEntry.setUpdatedAt(LocalDateTime.of(2026, 06, 11, 12, 00));

        secondLibraryEntry.setId(2L);
        secondLibraryEntry.setGame(secondGame);
        secondLibraryEntry.setUser(user);
        secondLibraryEntry.setGameStatus(GameStatus.PLAYING);
        secondLibraryEntry.setCreatedAt(LocalDateTime.of(2026, 07, 11, 12, 30));
        secondLibraryEntry.setUpdatedAt(LocalDateTime.of(2026, 07, 11, 12, 30));
    }

    @Test
    void getMyLibrary_WhenGivenGameStatus_ShouldReturnPageLibraryEntryResponse() {
        // Arrange
        when(authentication.getName()).thenReturn(user.getEmail());
        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));
        when(libraryEntryRepository.findByUserAndGameStatus(
                eq(user),
                any(Pageable.class),
                eq(GameStatus.WANT_TO_PLAY))).thenReturn(libraryPageSingleEntry);

        // Act
        Page<LibraryEntryResponse> response = libraryService.getMyLibrary(
                authentication,
                GameStatus.WANT_TO_PLAY,
                0,
                10);

        // Assert
        assertNotNull(response);
        assertEquals(1, response.getContent().size());
        assertEquals(game.getId(), response.getContent().get(0).getGameId());
        assertEquals(libraryEntry.getGameStatus(), response.getContent().get(0).getGameStatus());
        assertEquals(libraryEntry.getId(), response.getContent().get(0).getLibraryEntryId());

        // Verify
        verify(authentication).getName();
        verify(userRepository).findByEmail(user.getEmail());
        verify(libraryEntryRepository, never()).findByUser(any(User.class), any(Pageable.class));
        verify(libraryEntryRepository).findByUserAndGameStatus(
                eq(user),
                any(Pageable.class),
                eq(GameStatus.WANT_TO_PLAY));
    }

    @Test
    void getMyLibrary_WhenGameStatusIsNull_ShouldReturnPageLibraryEntryResponse() {
        // Arrange
        when(authentication.getName()).thenReturn(user.getEmail());
        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));
        when(libraryEntryRepository.findByUser(
                eq(user),
                any(Pageable.class))).thenReturn(libraryPageMultipleEntries);

        // Act
        Page<LibraryEntryResponse> response = libraryService.getMyLibrary(
                authentication,
                null,
                0,
                10);

        // Assert
        assertNotNull(response);
        assertEquals(2, response.getContent().size());

        // Verify
    }

    @Test
    void getMyLibrary_WhenUserDoesNotExists_ShouldThrowUserConflictException() {
        // Arrange
        when(authentication.getName()).thenReturn(user.getEmail());
        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.empty());

        // Act + Assert
        assertThrows(UserConflictException.class, () -> {
            libraryService.getMyLibrary(
                    authentication,
                    GameStatus.WANT_TO_PLAY,
                    0,
                    10);
        });

        // Verify
        verify(authentication).getName();
        verify(userRepository).findByEmail(user.getEmail());
        verify(libraryEntryRepository, never()).findByUserAndGameStatus(any(User.class), any(Pageable.class),
                any(GameStatus.class));
        verify(libraryEntryRepository, never()).findByUser(any(User.class), any(Pageable.class));
    }

    @Test
    void addEntryToLibrary_WhenUserDoesNotExists_ShouldThrowUserConflictException() {
        // Arrange
        when(authentication.getName()).thenReturn(user.getEmail());
        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.empty());

        // Act + Assert
        assertThrows(UserConflictException.class, () -> {
            libraryService.addEntryToLibrary(
                    authentication,
                    addLibraryEntryRequest);
        });

        // Verify
        verify(authentication).getName();
        verify(userRepository).findByEmail(user.getEmail());
        verify(gameRepository, never()).findById(anyLong());
        verify(libraryEntryRepository, never()).existsByUserIdAndGameId(
                anyLong(),
                anyLong());
        verify(libraryEntryRepository, never()).save(any(LibraryEntry.class));
    }

    @Test
    void addEntryToLibrary_WhenGameDoesNotExist_ShouldThrowGameConflictException() {
        // Arrange
        when(authentication.getName()).thenReturn(user.getEmail());
        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));
        when(gameRepository.findById(game.getId())).thenReturn(Optional.empty());

        // Act + Assert
        assertThrows(GameConflictException.class, () -> {
            libraryService.addEntryToLibrary(
                    authentication,
                    addLibraryEntryRequest);
        });

        // Verify
        verify(authentication).getName();
        verify(userRepository).findByEmail(user.getEmail());
        verify(gameRepository).findById(game.getId());
        verify(libraryEntryRepository, never()).existsByUserIdAndGameId(
                anyLong(),
                anyLong());
        verify(libraryEntryRepository, never()).save(any(LibraryEntry.class));
    }

    @Test
    void addEntryToLibrary_WhenLibraryEntryExists_ShouldThrowLibraryConflictException() {
        // Arrange
        when(authentication.getName()).thenReturn(user.getEmail());
        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));
        when(gameRepository.findById(game.getId())).thenReturn(Optional.of(game));
        when(libraryEntryRepository.existsByUserIdAndGameId(
                user.getId(),
                game.getId())).thenReturn(true);

        // Act + Assert
        assertThrows(LibraryConflictException.class, () -> {
            libraryService.addEntryToLibrary(
                    authentication,
                    addLibraryEntryRequest);
        });

        // Verify
        verify(authentication).getName();
        verify(userRepository).findByEmail(user.getEmail());
        verify(gameRepository).findById(game.getId());
        verify(libraryEntryRepository).existsByUserIdAndGameId(
                user.getId(),
                game.getId());
        verify(libraryEntryRepository, never()).save(any(LibraryEntry.class));
    }

    @Test
    void addEntryToLibrary_WhenLibraryEntryDoesNotExist_ShouldReturnLibraryEntryRespopnse() {
        // Arrange
        when(authentication.getName()).thenReturn(user.getEmail());
        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));
        when(gameRepository.findById(game.getId())).thenReturn(Optional.of(game));
        when(libraryEntryRepository.existsByUserIdAndGameId(
                user.getId(),
                game.getId())).thenReturn(false);
        when(libraryEntryRepository.save(any(LibraryEntry.class))).thenReturn(libraryEntry);

        // Act
        LibraryEntryResponse response = libraryService.addEntryToLibrary(
                authentication,
                addLibraryEntryRequest);

        // Assert
        assertNotNull(response);
        assertEquals(game.getId(), response.getGameId());
        assertEquals(libraryEntry.getId(), response.getLibraryEntryId());

        // Verify
        verify(authentication).getName();
        verify(userRepository).findByEmail(user.getEmail());
        verify(gameRepository).findById(game.getId());
        verify(libraryEntryRepository).existsByUserIdAndGameId(
                user.getId(),
                game.getId());
        verify(libraryEntryRepository).save(any(LibraryEntry.class));
    }

    @Test
    void updateGameStatus_WhenUserDoesNotExist_ShouldThrowUserConflictException() {
        // Arrange
        when(authentication.getName()).thenReturn(user.getEmail());
        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.empty());

        // Act + Assert
        assertThrows(UserConflictException.class, () -> {
            libraryService.updateGameStatus(
                    authentication,
                    libraryEntry.getId(),
                    GameStatus.PLAYING);
        });

        // Verify
        verify(authentication).getName();
        verify(userRepository).findByEmail(user.getEmail());
        verify(libraryEntryRepository, never()).findByIdAndUserId(
                anyLong(),
                anyLong());
        verify(libraryEntryRepository, never()).save(any(LibraryEntry.class));
    }

    @Test
    void updateGameStatus_WhenLibraryEntryDoesNotExist_ShouldThrowLibraryConflictException() {
        // Arrange
        when(authentication.getName()).thenReturn(user.getEmail());
        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));
        when(libraryEntryRepository.findByIdAndUserId(
                libraryEntry.getId(),
                user.getId())).thenReturn(Optional.empty());

        // Act + Assert
        assertThrows(LibraryConflictException.class, () -> {
            libraryService.updateGameStatus(
                    authentication,
                    libraryEntry.getId(),
                    GameStatus.PLAYING);
        });

        // Verify
        verify(authentication).getName();
        verify(userRepository).findByEmail(user.getEmail());
        verify(libraryEntryRepository).findByIdAndUserId(
                libraryEntry.getId(),
                user.getId());
        verify(libraryEntryRepository, never()).save(any(LibraryEntry.class));
    }

    @Test
    void updateGameStatus_WhenLibraryEntryExists_ShouldReturnLibraryEntryResponse() {
        // Arrange
        when(authentication.getName()).thenReturn(user.getEmail());
        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));
        when(libraryEntryRepository.findByIdAndUserId(
            libraryEntry.getId(),
            user.getId())).thenReturn(Optional.of(libraryEntry));
        when(libraryEntryRepository.save(any(LibraryEntry.class))).thenReturn(libraryEntry);

        // Act
        LibraryEntryResponse response = libraryService.updateGameStatus(
            authentication,
            libraryEntry.getId(),
            GameStatus.PLAYING);
        
        // Assert
        assertNotNull(response);
        assertEquals(game.getId(), response.getGameId());
        assertEquals(libraryEntry.getId(), response.getLibraryEntryId());
        assertEquals(GameStatus.PLAYING, response.getGameStatus());

        // Verify
        verify(authentication).getName();
        verify(userRepository).findByEmail(user.getEmail());
        verify(libraryEntryRepository).findByIdAndUserId(libraryEntry.getId(), user.getId());
        verify(libraryEntryRepository).save(any(LibraryEntry.class));
    }

    @Test
    void deleteEntry_WhenUserDoesNotExist_ShouldThrowUserConflictException() {
        // Arrange
        when(authentication.getName()).thenReturn(user.getEmail());
        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.empty());

        // Act + Assert
        assertThrows(UserConflictException.class, () -> {
            libraryService.deleteEntry(authentication, libraryEntry.getId());
        });

        // Verify
        verify(authentication).getName();
        verify(userRepository).findByEmail(user.getEmail());
        verify(libraryEntryRepository, never()).findByIdAndUserId(anyLong(), anyLong());
        verify(libraryEntryRepository, never()).delete(any(LibraryEntry.class));
    }

    @Test
    void deleteEntry_WhenLibraryEntryDoesNotExist_ShouldThrowLibraryConflictException() {
        // Arrange
        when(authentication.getName()).thenReturn(user.getEmail());
        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));
        when(libraryEntryRepository.findByIdAndUserId(libraryEntry.getId(), user.getId())).thenReturn(Optional.empty());

        // Act + Assert
        assertThrows(LibraryConflictException.class, () -> {
            libraryService.deleteEntry(authentication, libraryEntry.getId());
        });

        // Verify
        verify(authentication).getName();
        verify(userRepository).findByEmail(user.getEmail());
        verify(libraryEntryRepository).findByIdAndUserId(libraryEntry.getId(), user.getId());
        verify(libraryEntryRepository, never()).delete(any(LibraryEntry.class));
    }

    @Test
    void deleteEntry_WhenLibraryEntryExists_ShouldDeleteSuccessfully() {
        // Arrange
        when(authentication.getName()).thenReturn(user.getEmail());
        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));
        when(libraryEntryRepository.findByIdAndUserId(libraryEntry.getId(), user.getId())).thenReturn(Optional.of(libraryEntry));

        // Act
        libraryService.deleteEntry(authentication, libraryEntry.getId());

        // Verify
        verify(authentication).getName();
        verify(userRepository).findByEmail(user.getEmail());
        verify(libraryEntryRepository).findByIdAndUserId(libraryEntry.getId(), user.getId());
        verify(libraryEntryRepository).delete(libraryEntry);
    }
}
