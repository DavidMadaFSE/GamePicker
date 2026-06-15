package com.david.nextplay.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.david.nextplay.entity.User;

public interface UserRepository extends JpaRepository<User, Long> {
    
    Optional<User> findByEmail(String email);

    boolean existsByEmail(String email);

    boolean existsByUsername(String username);
}
