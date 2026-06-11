package com.david.nextplay.controller;

import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.david.nextplay.dto.library.AddLibraryEntryRequest;
import com.david.nextplay.dto.library.LibraryEntryResponse;
import com.david.nextplay.dto.library.UpdateGameStatusRequest;
import com.david.nextplay.enums.GameStatus;
import com.david.nextplay.service.LibraryService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/library")
public class LibraryController {

    private final LibraryService libraryService;

    @GetMapping
    public ResponseEntity<Page<LibraryEntryResponse>> getMyLibrary(
            Authentication authentication,
            @RequestParam(required = false) GameStatus gameStatus,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Page<LibraryEntryResponse> response = libraryService.getMyLibrary(authentication, gameStatus, page, size);

        return ResponseEntity.ok(response);
    }

    @PostMapping
    public ResponseEntity<LibraryEntryResponse> addEntryToLibrary(
            Authentication authentication,
            @Valid @RequestBody AddLibraryEntryRequest request) {
        LibraryEntryResponse response = libraryService.addEntryToLibrary(authentication, request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<LibraryEntryResponse> updateGameStatus(
            Authentication authentication,
            @PathVariable Long id,
            @RequestBody UpdateGameStatusRequest request) {
        LibraryEntryResponse response = libraryService.updateGameStatus(authentication, id, request.getGameStatus());

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteEntry(
            Authentication authentication,
            @PathVariable Long id) {
        libraryService.deleteEntry(authentication, id);

        return ResponseEntity
                .noContent()
                .build();
    }

}
