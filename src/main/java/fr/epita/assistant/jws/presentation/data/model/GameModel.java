package fr.epita.assistant.jws.presentation.data.model;

import fr.epita.assistant.jws.presentation.utils.GameState;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.With;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

@Entity @Table (name = "game")
@AllArgsConstructor @NoArgsConstructor @With @ToString
public class GameModel {
    public @Id @GeneratedValue(strategy = GenerationType.IDENTITY) Long id;
    public LocalDateTime startTime;
    public GameState state;
    public String map;
    public @OneToMany (cascade = CascadeType.ALL)List<PlayerModel> players;
}
