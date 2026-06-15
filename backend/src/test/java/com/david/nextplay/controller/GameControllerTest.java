package com.david.nextplay.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.david.nextplay.dto.game.CreateGameRequest;
import com.david.nextplay.dto.game.GameResponse;
import com.david.nextplay.enums.Genre;
import com.david.nextplay.enums.Platform;
import com.david.nextplay.exception.GameConflictException;
import com.david.nextplay.service.CustomUserDetailsService;
import com.david.nextplay.service.GameService;
import com.david.nextplay.service.JwtService;

import jakarta.persistence.EntityNotFoundException;

@WebMvcTest(GameController.class)
@AutoConfigureMockMvc(addFilters = false)
public class GameControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private GameService gameService;

    @MockitoBean
    private JwtService jwtService;

    @MockitoBean
    private CustomUserDetailsService customUserDetailsService;

    private GameResponse gameResponse;
    private GameResponse secondGameResponse;
    private String createGameJson;

    @BeforeEach
    void setUp() {
        gameResponse = new GameResponse(
                1L,
                "Minecraft",
                "A sandbox survival game.",
                LocalDate.of(2011, 11, 17),
                "http://minecraft.jpg",
                5.0,
                List.of(Genre.ADVENTURE, Genre.SIMULATION),
                List.of(Platform.PC, Platform.XBOX));

        secondGameResponse = new GameResponse(
                2L,
                "Elden Ring",
                "An open-world action RPG.",
                LocalDate.of(2022, 2, 25),
                "http://eldenring.jpg",
                4.8,
                List.of(Genre.RPG, Genre.ACTION),
                List.of(Platform.PC, Platform.PLAYSTATION));

        createGameJson = """
                {
                    "title": "Minecraft",
                    "description": "A sandbox survival game.",
                    "releaseDate": "2011-11-17",
                    "coverImageUrl": "http://minecraft.jpg",
                    "genres": ["ADVENTURE", "SIMULATION"],
                    "platforms": ["PC", "XBOX"]
                }
                """;
    }

    @Test
    void getGames_WhenCalled_ShouldReturnPageGameResponse() throws Exception {
        // Arrange
        Page<GameResponse> gamePage = new PageImpl<>(List.of(gameResponse, secondGameResponse));
        when(gameService.getGames(
                eq("mine"),
                eq(LocalDate.of(2011, 11, 17)),
                eq(Genre.ADVENTURE),
                eq(Platform.PC),
                eq(0),
                eq(10),
                eq("title"),
                eq("asc"))).thenReturn(gamePage);

        // Act + Assert
        mockMvc.perform(get("/api/games")
                .param("title", "mine")
                .param("releaseDate", "2011-11-17")
                .param("genre", "ADVENTURE")
                .param("platform", "PC")
                .param("page", "0")
                .param("size", "10")
                .param("sortBy", "title")
                .param("sortDir", "asc"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id").value(gameResponse.getId()))
                .andExpect(jsonPath("$.content[0].title").value(gameResponse.getTitle()))
                .andExpect(jsonPath("$.content[1].id").value(secondGameResponse.getId()));

        // Verify
        verify(gameService).getGames(
                eq("mine"),
                eq(LocalDate.of(2011, 11, 17)),
                eq(Genre.ADVENTURE),
                eq(Platform.PC),
                eq(0),
                eq(10),
                eq("title"),
                eq("asc"));
    }

    @Test
    void getGameById_WhenGameExists_ShouldReturnGameResponse() throws Exception {
        // Arrange
        when(gameService.getGameById(gameResponse.getId())).thenReturn(gameResponse);

        // Act + Assert
        mockMvc.perform(get("/api/games/{id}", gameResponse.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(gameResponse.getId()))
                .andExpect(jsonPath("$.title").value(gameResponse.getTitle()))
                .andExpect(jsonPath("$.averageRating").value(gameResponse.getAverageRating()));

        // Verify
        verify(gameService).getGameById(gameResponse.getId());
    }

    @Test
    void getGameById_WhenGameDoesNotExist_ShouldReturnNotFound() throws Exception {
        // Arrange
        when(gameService.getGameById(gameResponse.getId()))
                .thenThrow(new EntityNotFoundException("Game not found."));

        // Act + Assert
        mockMvc.perform(get("/api/games/{id}", gameResponse.getId()))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Game not found."))
                .andExpect(jsonPath("$.status").value(404));

        // Verify
        verify(gameService).getGameById(gameResponse.getId());
    }

    @Test
    void createGame_WhenRequestIsValid_ShouldReturnCreatedGameResponse() throws Exception {
        // Arrange
        when(gameService.createGame(any(CreateGameRequest.class))).thenReturn(gameResponse);

        // Act + Assert
        mockMvc.perform(post("/api/games")
                .contentType(MediaType.APPLICATION_JSON)
                .content(createGameJson))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(gameResponse.getId()))
                .andExpect(jsonPath("$.title").value(gameResponse.getTitle()));

        // Verify
        verify(gameService).createGame(any(CreateGameRequest.class));
    }

    @Test
    void createGame_WhenRequestIsInvalid_ShouldReturnBadRequest() throws Exception {
        // Arrange
        String invalidJson = """
                {
                    "title": "",
                    "description": "A sandbox survival game.",
                    "releaseDate": "2011-11-17",
                    "genres": ["ADVENTURE"],
                    "platforms": ["PC"]
                }
                """;

        // Act + Assert
        mockMvc.perform(post("/api/games")
                .contentType(MediaType.APPLICATION_JSON)
                .content(invalidJson))
                .andExpect(status().isBadRequest());

        // Verify
        verify(gameService, never()).createGame(any(CreateGameRequest.class));
    }

    @Test
    void createGame_WhenGameConflictOccurs_ShouldReturnConflict() throws Exception {
        // Arrange
        when(gameService.createGame(any(CreateGameRequest.class)))
                .thenThrow(new GameConflictException("Game already exists."));

        // Act + Assert
        mockMvc.perform(post("/api/games")
                .contentType(MediaType.APPLICATION_JSON)
                .content(createGameJson))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message").value("Game already exists."))
                .andExpect(jsonPath("$.status").value(409));

        // Verify
        verify(gameService).createGame(any(CreateGameRequest.class));
    }

    @Test
    void createGames_WhenRequestsAreValid_ShouldReturnCreatedGameResponses() throws Exception {
        // Arrange
        when(gameService.createGames(any())).thenReturn(List.of(gameResponse, secondGameResponse));

        // Act + Assert
        mockMvc.perform(post("/api/games/bulk")
                .contentType(MediaType.APPLICATION_JSON)
                .content("[" + createGameJson + "," + createGameJson + "]"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$[0].id").value(gameResponse.getId()))
                .andExpect(jsonPath("$[1].id").value(secondGameResponse.getId()));

        // Verify
        verify(gameService).createGames(any());
    }

    @Test
    void updateGame_WhenRequestIsValid_ShouldReturnGameResponse() throws Exception {
        // Arrange
        when(gameService.updateGame(eq(gameResponse.getId()), any(CreateGameRequest.class))).thenReturn(gameResponse);

        // Act + Assert
        mockMvc.perform(put("/api/games/{id}", gameResponse.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(createGameJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(gameResponse.getId()))
                .andExpect(jsonPath("$.title").value(gameResponse.getTitle()));

        // Verify
        verify(gameService).updateGame(eq(gameResponse.getId()), any(CreateGameRequest.class));
    }

    @Test
    void deleteGame_WhenGameExists_ShouldReturnNoContent() throws Exception {
        // Arrange
        doNothing().when(gameService).deleteGame(gameResponse.getId());

        // Act + Assert
        mockMvc.perform(delete("/api/games/{id}", gameResponse.getId()))
                .andExpect(status().isNoContent());

        // Verify
        verify(gameService).deleteGame(gameResponse.getId());
    }
}
