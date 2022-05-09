package fr.epita.assistant.jws.presentation.data.model;

import fr.epita.assistant.jws.presentation.utils.GameState;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.With;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity @Table(name = "player")
@AllArgsConstructor @NoArgsConstructor @With
public class PlayerModel {
    public @Id @GeneratedValue(strategy = GenerationType.IDENTITY) Long id;
    public LocalDateTime lastBomb;
    public LocalDateTime lastMove;
    public int lives;
    public String name;
    public int posx;
    public int posy;
    public @ManyToOne GameModel game_id;
}
