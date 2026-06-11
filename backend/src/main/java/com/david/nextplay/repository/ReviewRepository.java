package com.david.nextplay.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.david.nextplay.entity.Review;

public interface ReviewRepository extends JpaRepository<Review, Long> {
    
    Page<Review> findByGameId(Long gameId, Pageable pageable);

    Page<Review> findByUserId(Long userId, Pageable pageable);

    Optional<Review> findByUserIdAndGameId(Long userId, Long gameId);

    Optional<Review> findByUserIdAndId(Long userId, Long id);

    boolean existsByUserIdAndGameId(Long userId, Long gameId);

    @Query("SELECT AVG(r.rating) FROM Review r WHERE r.game.id = :gameId")
    Double findAverageRatingByGameId(@Param("gameId") Long gameId);
}
