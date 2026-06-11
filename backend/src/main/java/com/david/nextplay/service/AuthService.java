package com.david.nextplay.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.david.nextplay.dto.auth.AuthResponse;
import com.david.nextplay.dto.auth.LoginRequest;
import com.david.nextplay.dto.auth.RegisterRequest;
import com.david.nextplay.dto.user.UserResponse;
import com.david.nextplay.entity.User;
import com.david.nextplay.exception.UnauthorizedException;
import com.david.nextplay.exception.UserConflictException;
import com.david.nextplay.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    @Transactional
    public UserResponse register(RegisterRequest request) {
        boolean email = userRepository.existsByEmail(request.getEmail());
        boolean username = userRepository.existsByUsername(request.getUsername());

        if (email) {
            throw new UserConflictException("User already exists with email: " + email);
        }

        if (username) {
            throw new UserConflictException("User already exists with username: " + username);
        }

        String hashedPassword = passwordEncoder.encode(request.getPassword());

        User user = new User();

        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setPassword(hashedPassword);

        User savedUser = userRepository.save(user);

        return new UserResponse(
                savedUser.getId(),
                savedUser.getDisplayUsername(),
                savedUser.getEmail(),
                savedUser.getRole(),
                savedUser.getCreatedAt(),
                savedUser.getUpdatedAt());
    }

    @Transactional(readOnly = true)
    public AuthResponse login(LoginRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new UnauthorizedException("Invalid Credentials"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new UnauthorizedException("Invalid Credentials");
        }

        String token = jwtService.generateToken(user);

        return new AuthResponse(
                token,
                user.getId(),
                user.getDisplayUsername(),
                user.getEmail(),
                user.getRole());
    }
}
