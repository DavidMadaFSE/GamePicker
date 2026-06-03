package com.david.nextplay.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.david.nextplay.entity.LibraryEntry;
import com.david.nextplay.entity.User;
import com.david.nextplay.enums.GameStatus;

@Repository
public interface LibraryEntryRepository extends JpaRepository<LibraryEntry, Long> {
    
    Page<LibraryEntry> findByUser(User user, Pageable pageable);

    Page<LibraryEntry> findByUserAndGameStatus(User user, Pageable pageable, GameStatus gameStatus);

    Optional <LibraryEntry> findByIdAndUserId(Long id, Long userId);

    boolean existsByUserIdAndGameId(Long userId, Long gameId);

    void deleteByUserIdAndGameId(Long userId, Long gameId);
}
