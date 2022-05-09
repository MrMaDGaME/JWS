package fr.epita.assistant.jws.presentation.domain.entity;

import fr.epita.assistant.jws.presentation.utils.GameState;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.time.LocalDateTime;
import java.util.List;

@AllArgsConstructor @NoArgsConstructor @ToString
public class GameEntity {
    public Long id;
    public LocalDateTime startTime;
    public GameState state;
    public String map;
    public List<PlayerEntity> players;
}
