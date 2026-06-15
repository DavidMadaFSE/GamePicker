package com.david.nextplay.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.nullable;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.david.nextplay.dto.library.AddLibraryEntryRequest;
import com.david.nextplay.dto.library.LibraryEntryResponse;
import com.david.nextplay.enums.GameStatus;
import com.david.nextplay.exception.LibraryConflictException;
import com.david.nextplay.service.CustomUserDetailsService;
import com.david.nextplay.service.JwtService;
import com.david.nextplay.service.LibraryService;

@WebMvcTest(LibraryController.class)
@AutoConfigureMockMvc(addFilters = false)
public class LibraryControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private LibraryService libraryService;

    @MockitoBean
    private JwtService jwtService;

    @MockitoBean
    private CustomUserDetailsService customUserDetailsService;

    private LibraryEntryResponse libraryEntryResponse;
    private String addLibraryEntryJson;
    private String updateGameStatusJson;

    @BeforeEach
    void setUp() {
        libraryEntryResponse = new LibraryEntryResponse(
                1L,
                1L,
                "Minecraft",
                "http://minecraft.jpg",
                5.0,
                GameStatus.PLAYING,
                LocalDateTime.of(2026, 6, 12, 12, 0),
                LocalDateTime.of(2026, 6, 12, 12, 0));

        addLibraryEntryJson = """
                {
                    "gameId": 1,
                    "gameStatus": "PLAYING"
                }
                """;

        updateGameStatusJson = """
                {
                    "gameStatus": "COMPLETED"
                }
                """;
    }

    @Test
    void getMyLibrary_WhenCalled_ShouldReturnPageLibraryEntryResponse() throws Exception {
        // Arrange
        Page<LibraryEntryResponse> libraryPage = new PageImpl<>(List.of(libraryEntryResponse));
        when(libraryService.getMyLibrary(
                nullable(Authentication.class),
                eq(GameStatus.PLAYING),
                eq(0),
                eq(10))).thenReturn(libraryPage);

        // Act + Assert
        mockMvc.perform(get("/api/library")
                .param("gameStatus", "PLAYING")
                .param("page", "0")
                .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].libraryEntryId").value(libraryEntryResponse.getLibraryEntryId()))
                .andExpect(jsonPath("$.content[0].gameId").value(libraryEntryResponse.getGameId()))
                .andExpect(jsonPath("$.content[0].gameStatus").value(libraryEntryResponse.getGameStatus().name()));

        // Verify
        verify(libraryService).getMyLibrary(
                nullable(Authentication.class),
                eq(GameStatus.PLAYING),
                eq(0),
                eq(10));
    }

    @Test
    void addEntryToLibrary_WhenRequestIsValid_ShouldReturnCreatedLibraryEntryResponse() throws Exception {
        // Arrange
        when(libraryService.addEntryToLibrary(nullable(Authentication.class), any(AddLibraryEntryRequest.class)))
                .thenReturn(libraryEntryResponse);

        // Act + Assert
        mockMvc.perform(post("/api/library")
                .contentType(MediaType.APPLICATION_JSON)
                .content(addLibraryEntryJson))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.libraryEntryId").value(libraryEntryResponse.getLibraryEntryId()))
                .andExpect(jsonPath("$.gameId").value(libraryEntryResponse.getGameId()))
                .andExpect(jsonPath("$.gameStatus").value(libraryEntryResponse.getGameStatus().name()));

        // Verify
        verify(libraryService).addEntryToLibrary(nullable(Authentication.class), any(AddLibraryEntryRequest.class));
    }

    @Test
    void addEntryToLibrary_WhenRequestIsInvalid_ShouldReturnBadRequest() throws Exception {
        // Arrange
        String invalidJson = """
                {
                    "gameStatus": "PLAYING"
                }
                """;

        // Act + Assert
        mockMvc.perform(post("/api/library")
                .contentType(MediaType.APPLICATION_JSON)
                .content(invalidJson))
                .andExpect(status().isBadRequest());

        // Verify
        verify(libraryService, never()).addEntryToLibrary(nullable(Authentication.class), any(AddLibraryEntryRequest.class));
    }

    @Test
    void addEntryToLibrary_WhenLibraryConflictOccurs_ShouldReturnConflict() throws Exception {
        // Arrange
        when(libraryService.addEntryToLibrary(nullable(Authentication.class), any(AddLibraryEntryRequest.class)))
                .thenThrow(new LibraryConflictException("Game already exists in library."));

        // Act + Assert
        mockMvc.perform(post("/api/library")
                .contentType(MediaType.APPLICATION_JSON)
                .content(addLibraryEntryJson))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message").value("Game already exists in library."))
                .andExpect(jsonPath("$.status").value(409));

        // Verify
        verify(libraryService).addEntryToLibrary(nullable(Authentication.class), any(AddLibraryEntryRequest.class));
    }

    @Test
    void updateGameStatus_WhenRequestIsValid_ShouldReturnLibraryEntryResponse() throws Exception {
        // Arrange
        libraryEntryResponse.setGameStatus(GameStatus.COMPLETED);
        when(libraryService.updateGameStatus(
                nullable(Authentication.class),
                eq(libraryEntryResponse.getLibraryEntryId()),
                eq(GameStatus.COMPLETED))).thenReturn(libraryEntryResponse);

        // Act + Assert
        mockMvc.perform(patch("/api/library/{id}", libraryEntryResponse.getLibraryEntryId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(updateGameStatusJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.libraryEntryId").value(libraryEntryResponse.getLibraryEntryId()))
                .andExpect(jsonPath("$.gameStatus").value(GameStatus.COMPLETED.name()));

        // Verify
        verify(libraryService).updateGameStatus(
                nullable(Authentication.class),
                eq(libraryEntryResponse.getLibraryEntryId()),
                eq(GameStatus.COMPLETED));
    }

    @Test
    void deleteEntry_WhenEntryExists_ShouldReturnNoContent() throws Exception {
        // Arrange
        doNothing().when(libraryService).deleteEntry(nullable(Authentication.class), eq(libraryEntryResponse.getLibraryEntryId()));

        // Act + Assert
        mockMvc.perform(delete("/api/library/{id}", libraryEntryResponse.getLibraryEntryId()))
                .andExpect(status().isNoContent());

        // Verify
        verify(libraryService).deleteEntry(nullable(Authentication.class), eq(libraryEntryResponse.getLibraryEntryId()));
    }
}
