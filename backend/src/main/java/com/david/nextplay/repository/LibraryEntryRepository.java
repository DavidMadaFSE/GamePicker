package com.david.nextplay.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.david.nextplay.entity.LibraryEntry;
import com.david.nextplay.entity.User;
import com.david.nextplay.enums.GameStatus;

public interface LibraryEntryRepository extends JpaRepository<LibraryEntry, Long> {
    
    Page<LibraryEntry> findByUser(User user, Pageable pageable);

    Page<LibraryEntry> findByUserAndGameStatus(User user, Pageable pageable, GameStatus gameStatus);

    List<LibraryEntry> findByUserId(Long userId);

    Optional<LibraryEntry> findByIdAndUserId(Long id, Long userId);

    Optional<LibraryEntry> findByUserIdAndGameId(Long userId, Long gameId);

    boolean existsByUserIdAndGameId(Long userId, Long gameId);

    void deleteByUserIdAndGameId(Long userId, Long gameId);
}
