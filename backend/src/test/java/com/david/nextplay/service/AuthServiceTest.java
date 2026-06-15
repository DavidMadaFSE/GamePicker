package com.david.nextplay.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.david.nextplay.dto.auth.AuthResponse;
import com.david.nextplay.dto.auth.LoginRequest;
import com.david.nextplay.dto.auth.RegisterRequest;
import com.david.nextplay.dto.user.UserResponse;
import com.david.nextplay.entity.User;
import com.david.nextplay.enums.Role;
import com.david.nextplay.exception.UnauthorizedException;
import com.david.nextplay.exception.UserConflictException;
import com.david.nextplay.repository.UserRepository;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {
    
    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtService jwtService;

    @InjectMocks
    private AuthService authService;

    private RegisterRequest registerRequest;
    private LoginRequest loginRequest;
    private User user;

    @BeforeEach
    void setUp() {
        registerRequest = new RegisterRequest();
        loginRequest = new LoginRequest();
        user = new User();

        registerRequest.setUsername("davidmada1");
        registerRequest.setEmail("david@example.com");
        registerRequest.setPassword("password123");

        loginRequest.setEmail("david@example.com");
        loginRequest.setPassword("password123");

        user.setId(1L);
        user.setUsername("davidmada1");
        user.setEmail("david@example.com");
        user.setPassword("encodedPassword");
        user.setRole(Role.USER);
        user.setCreatedAt(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());
    }

    @Test
    void register_WhenUsernameExists_ShouldThrowUserConflictException() {
        // Arrange
        when(userRepository.existsByEmail(registerRequest.getEmail())).thenReturn(false);
        when(userRepository.existsByUsername(registerRequest.getUsername())).thenReturn(true);

        // Act + Assert
        assertThrows(UserConflictException.class, () -> {
            authService.register(registerRequest);
        });

        // Verify
        verify(userRepository).existsByEmail(registerRequest.getEmail());
        verify(userRepository).existsByUsername(registerRequest.getUsername());
        verify(userRepository, never()).save(any(User.class));
        verify(passwordEncoder, never()).encode(anyString());
    }

    @Test
    void register_WhenEmailExists_ShouldThrowUserConflictException() {
        // Arrange
        when(userRepository.existsByEmail(registerRequest.getEmail())).thenReturn(true);

        // Act + Assert
        assertThrows(UserConflictException.class, () -> {
            authService.register(registerRequest);
        });

        // Verify
        verify(userRepository).existsByEmail(registerRequest.getEmail());
        verify(userRepository, never()).existsByUsername(anyString());
        verify(userRepository, never()).save(any(User.class));
        verify(passwordEncoder, never()).encode(anyString());
    }

    @Test
    void register_WhenEmailAndUsernameDoesNotExist_ShouldCreateUserAndReturnUserResponse() {
        // Arrange
        when(userRepository.existsByEmail(registerRequest.getEmail())).thenReturn(false);
        when(passwordEncoder.encode(registerRequest.getPassword())).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenReturn(user);

        // Act
        UserResponse userResponse = authService.register(registerRequest);

        // Assert
        assertEquals(user.getId(), userResponse.getId());
        assertEquals(user.getDisplayUsername(), userResponse.getUsername());
        assertEquals(user.getEmail(), userResponse.getEmail());
        assertEquals(user.getRole(), userResponse.getRole());

        // Verify
        verify(userRepository).existsByEmail(registerRequest.getEmail());
        verify(passwordEncoder).encode(registerRequest.getPassword());
        verify(userRepository).save(any(User.class));
    }

    @Test
    void login_WhenEmailDoesNotExist_ShouldThrowUnauthorizedException() {
        // Arrange
        when(userRepository.findByEmail(registerRequest.getEmail())).thenReturn(Optional.empty());

        // Act + Assert
        assertThrows(UnauthorizedException.class, () -> {
            authService.login(loginRequest);
        });

        // Verify
        verify(userRepository).findByEmail(loginRequest.getEmail());
        verify(passwordEncoder, never()).matches(anyString(), anyString());
        verify(jwtService, never()).generateToken(any(User.class));
    }

    @Test
    void login_WhenPasswordDoesNotMatch_ShouldThrowUnauthorizedException() {
        // Arrange
        when(userRepository.findByEmail(loginRequest.getEmail())).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())).thenReturn(false);

        // Act + Assert
        assertThrows(UnauthorizedException.class, () -> {
            authService.login(loginRequest);
        });

        // Verify
        verify(userRepository).findByEmail(loginRequest.getEmail());
        verify(passwordEncoder).matches(loginRequest.getPassword(), user.getPassword());
        verify(jwtService, never()).generateToken(any(User.class));
    }

    @Test
    void login_WhenCredentialsAreValid_ShouldReturnAuthResponse() {
        // Arrange
        when(userRepository.findByEmail(loginRequest.getEmail())).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())).thenReturn(true);
        when (jwtService.generateToken(user)).thenReturn("fake-jwt-token");

        // Act
        AuthResponse response = authService.login(loginRequest);

        //Assert
        assertEquals(user.getDisplayUsername(), response.getUsername());
        assertEquals(user.getEmail(), response.getEmail());
        assertEquals(user.getRole(), response.getRole());
        assertEquals("fake-jwt-token", response.getToken());

        //Verify
        verify(userRepository).findByEmail(loginRequest.getEmail());
        verify(passwordEncoder).matches(loginRequest.getPassword(), user.getPassword());
        verify(jwtService).generateToken(any(User.class));
    }
}
