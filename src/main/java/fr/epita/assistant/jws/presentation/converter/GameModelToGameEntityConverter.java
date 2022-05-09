package fr.epita.assistant.jws.presentation.converter;


import fr.epita.assistant.jws.presentation.data.model.GameModel;
import fr.epita.assistant.jws.presentation.data.model.PlayerModel;
import fr.epita.assistant.jws.presentation.domain.entity.GameEntity;
import fr.epita.assistant.jws.presentation.domain.entity.PlayerEntity;
import lombok.AllArgsConstructor;

import javax.enterprise.context.ApplicationScoped;
import java.util.stream.Collectors;

@ApplicationScoped
public class GameModelToGameEntityConverter {
    public GameEntity convert(GameModel model){
        return new GameEntity(
                model.id,
                model.startTime,
                model.state,
                model.map,
                model.players.stream().map(player -> convertPlayer(player)).collect(Collectors.toList()));
    }

    public PlayerEntity convertPlayer(PlayerModel player) {
        return new PlayerEntity(
                player.id,
                player.lastBomb,
                player.lastMove,
                player.lives,
                player.name,
                player.posx,
                player.posy
                );
    }
}
