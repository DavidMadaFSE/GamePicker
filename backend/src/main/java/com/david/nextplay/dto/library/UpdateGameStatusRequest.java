package com.david.nextplay.dto.library;

import com.david.nextplay.enums.GameStatus;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateGameStatusRequest {
    
    @NotNull
    private GameStatus gameStatus;
}
