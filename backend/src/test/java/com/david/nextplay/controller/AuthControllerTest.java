package com.david.nextplay.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDateTime;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.david.nextplay.dto.auth.AuthResponse;
import com.david.nextplay.dto.auth.LoginRequest;
import com.david.nextplay.dto.auth.RegisterRequest;
import com.david.nextplay.dto.user.UserResponse;
import com.david.nextplay.enums.Role;
import com.david.nextplay.exception.UnauthorizedException;
import com.david.nextplay.exception.UserConflictException;
import com.david.nextplay.service.AuthService;
import com.david.nextplay.service.CustomUserDetailsService;
import com.david.nextplay.service.JwtService;
import com.fasterxml.jackson.databind.ObjectMapper;

@WebMvcTest(AuthController.class)
@AutoConfigureMockMvc(addFilters = false)
public class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AuthService authService;

    @MockitoBean
    private JwtService jwtService;

    @MockitoBean
    private CustomUserDetailsService customUserDetailsService;

    private final ObjectMapper objectMapper = new ObjectMapper();

    private RegisterRequest registerRequest;
    private LoginRequest loginRequest;
    private UserResponse userResponse;
    private AuthResponse authResponse;

    @BeforeEach
    void setUp() {
        registerRequest = new RegisterRequest();
        loginRequest = new LoginRequest();

        registerRequest.setUsername("davidmada1");
        registerRequest.setEmail("david@example.com");
        registerRequest.setPassword("password123");

        loginRequest.setEmail("david@example.com");
        loginRequest.setPassword("password123");

        userResponse = new UserResponse(
                1L,
                "davidmada1",
                "david@example.com",
                Role.USER,
                LocalDateTime.of(2026, 6, 12, 12, 0),
                LocalDateTime.of(2026, 6, 12, 12, 0));

        authResponse = new AuthResponse(
                "fake-jwt-token",
                1L,
                "davidmada1",
                "david@example.com",
                Role.USER);
    }

    @Test
    void register_WhenRequestIsValid_ShouldReturnUserResponse() throws Exception {
        // Arrange
        when(authService.register(any(RegisterRequest.class))).thenReturn(userResponse);

        // Act + Assert
        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(registerRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(userResponse.getId()))
                .andExpect(jsonPath("$.username").value(userResponse.getUsername()))
                .andExpect(jsonPath("$.email").value(userResponse.getEmail()))
                .andExpect(jsonPath("$.role").value(userResponse.getRole().name()));

        // Verify
        verify(authService).register(any(RegisterRequest.class));
    }

    @Test
    void register_WhenRequestIsInvalid_ShouldReturnBadRequest() throws Exception {
        // Arrange
        registerRequest.setUsername("");

        // Act + Assert
        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(registerRequest)))
                .andExpect(status().isBadRequest());

        // Verify
        verify(authService, never()).register(any(RegisterRequest.class));
    }

    @Test
    void register_WhenUserConflictOccurs_ShouldReturnConflict() throws Exception {
        // Arrange
        when(authService.register(any(RegisterRequest.class)))
                .thenThrow(new UserConflictException("Email already exists."));

        // Act + Assert
        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(registerRequest)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message").value("Email already exists."))
                .andExpect(jsonPath("$.status").value(409));

        // Verify
        verify(authService).register(any(RegisterRequest.class));
    }

    @Test
    void login_WhenRequestIsValid_ShouldReturnAuthResponse() throws Exception {
        // Arrange
        when(authService.login(any(LoginRequest.class))).thenReturn(authResponse);

        // Act + Assert
        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value(authResponse.getToken()))
                .andExpect(jsonPath("$.userId").value(authResponse.getUserId()))
                .andExpect(jsonPath("$.username").value(authResponse.getUsername()))
                .andExpect(jsonPath("$.email").value(authResponse.getEmail()))
                .andExpect(jsonPath("$.role").value(authResponse.getRole().name()));

        // Verify
        verify(authService).login(any(LoginRequest.class));
    }

    @Test
    void login_WhenRequestIsInvalid_ShouldReturnBadRequest() throws Exception {
        // Arrange
        loginRequest.setPassword("");

        // Act + Assert
        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isBadRequest());

        // Verify
        verify(authService, never()).login(any(LoginRequest.class));
    }

    @Test
    void login_WhenUnauthorizedOccurs_ShouldReturnUnauthorized() throws Exception {
        // Arrange
        when(authService.login(any(LoginRequest.class)))
                .thenThrow(new UnauthorizedException("Invalid email or password."));

        // Act + Assert
        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message").value("Invalid email or password."))
                .andExpect(jsonPath("$.status").value(401));

        // Verify
        verify(authService).login(any(LoginRequest.class));
    }
}
