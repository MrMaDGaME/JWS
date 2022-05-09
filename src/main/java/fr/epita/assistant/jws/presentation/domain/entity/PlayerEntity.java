package fr.epita.assistant.jws.presentation.domain.entity;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@AllArgsConstructor @NoArgsConstructor
public class PlayerEntity {
    public Long id;
    public LocalDateTime lastBomb;
    public LocalDateTime lastMove;
    public int lives;
    public String name;
    public int posx;
    public int posy;
}
