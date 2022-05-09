package fr.epita.assistant.jws.presentation.rest.response;

import fr.epita.assistant.jws.presentation.domain.entity.PlayerEntity;
import fr.epita.assistant.jws.presentation.utils.GameState;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Value;
import lombok.With;

import java.time.LocalDateTime;
import java.util.List;

@Value @With
public class GameResponseDTO {
    Long id;
    int players;
    GameState state;
}
