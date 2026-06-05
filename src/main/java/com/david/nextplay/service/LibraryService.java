package com.david.nextplay.service;

import java.time.LocalDateTime;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.david.nextplay.dto.library.AddLibraryEntryRequest;
import com.david.nextplay.dto.library.LibraryEntryResponse;
import com.david.nextplay.entity.Game;
import com.david.nextplay.entity.LibraryEntry;
import com.david.nextplay.entity.User;
import com.david.nextplay.enums.GameStatus;
import com.david.nextplay.exception.GameConflictException;
import com.david.nextplay.exception.LibraryConflictException;
import com.david.nextplay.exception.UserConflictException;
import com.david.nextplay.repository.GameRepository;
import com.david.nextplay.repository.LibraryEntryRepository;
import com.david.nextplay.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class LibraryService {

    private final LibraryEntryRepository libraryEntryRepository;
    private final UserRepository userRepository;
    private final GameRepository gameRepository;

    @Transactional(readOnly = true)
    public Page<LibraryEntryResponse> getMyLibrary(
            Authentication authentication,
            GameStatus gameStatus,
            int page,
            int size) {
        String email = authentication.getName();

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserConflictException("User not found."));

        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());

        Page<LibraryEntry> entries;

        if (gameStatus != null) {
            entries = libraryEntryRepository.findByUserAndGameStatus(user, pageable, gameStatus);
        } else {
            entries = libraryEntryRepository.findByUser(user, pageable);
        }

        return entries.map(this::mapToResponse);
    }

    @Transactional
    public LibraryEntryResponse addEntryToLibrary(Authentication authentication, AddLibraryEntryRequest request) {
        String email = authentication.getName();

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserConflictException("User not found."));

        Game game = gameRepository.findById(request.getGameId())
                .orElseThrow(() -> new GameConflictException("Game not found."));

        boolean alreadyExists = libraryEntryRepository.existsByUserIdAndGameId(user.getId(), game.getId());

        if (alreadyExists) {
            throw new LibraryConflictException("Game is already in library.");
        }

        LibraryEntry entry = new LibraryEntry();
        entry.setUser(user);
        entry.setGame(game);
        entry.setGameStatus(request.getGameStatus());
        entry.setCreatedAt(LocalDateTime.now());
        entry.setUpdatedAt(LocalDateTime.now());

        LibraryEntry savedEntry = libraryEntryRepository.save(entry);

        return mapToResponse(savedEntry);
    }

    @Transactional
    public LibraryEntryResponse updateGameStatus(Authentication authentication, Long id, GameStatus gameStatus) {
        String email = authentication.getName();

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserConflictException("User not found."));

        LibraryEntry entry = libraryEntryRepository.findByIdAndUserId(id, user.getId())
                .orElseThrow(() -> new LibraryConflictException("Library entry not found."));

        entry.setGameStatus(gameStatus);
        entry.setUpdatedAt(LocalDateTime.now());

        LibraryEntry savedEntry = libraryEntryRepository.save(entry);

        return mapToResponse(savedEntry);
    }

    @Transactional
    public void deleteEntry(Authentication authentication, Long id) {
        String email = authentication.getName();

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserConflictException("User not found."));

        LibraryEntry entry = libraryEntryRepository.findByIdAndUserId(id, user.getId())
                .orElseThrow(() -> new LibraryConflictException("Library entry not found."));

        libraryEntryRepository.delete(entry);
    }

    private LibraryEntryResponse mapToResponse(LibraryEntry entry) {
        Game game = entry.getGame();

        return new LibraryEntryResponse(
                entry.getId(),
                game.getId(),
                game.getTitle(),
                game.getCoverImageUrl(),
                game.getAverageRating(),
                entry.getGameStatus(),
                entry.getCreatedAt(),
                entry.getUpdatedAt());
    }
}
