package com.david.nextplay.dto.library;

import java.time.LocalDateTime;

import com.david.nextplay.enums.GameStatus;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class LibraryEntryResponse {
    
    private Long libraryEntryId;
    private Long gameId;
    private String title;
    private String coverImageUrl;
    private Double averageRating;
    private GameStatus gameStatus;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
