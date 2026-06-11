package com.david.nextplay.dto.library;

import com.david.nextplay.enums.GameStatus;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AddLibraryEntryRequest {
    
    @NotNull
    private Long gameId;

    @NotNull
    private GameStatus gameStatus = GameStatus.WANT_TO_PLAY;
}
