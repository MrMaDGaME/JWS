package fr.epita.assistant.jws.presentation.converter;

import fr.epita.assistant.jws.presentation.domain.entity.GameEntity;
import fr.epita.assistant.jws.presentation.domain.entity.PlayerEntity;
import fr.epita.assistant.jws.presentation.rest.response.GameDetailResponseDTO;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@ApplicationScoped
public class GameEntityToGameDetailResponseDTO {
    @Inject RleCoder coder;

    public GameDetailResponseDTO convert (GameEntity entity) {
        return new GameDetailResponseDTO(
                entity.startTime,
                entity.state,
                entity.players.stream().map(this::convertPlayer).collect(Collectors.toList()),
                coder.rleEncoded(entity.map),
                entity.id);
    }

    public GameDetailResponseDTO.PlayerResponseDTO convertPlayer(PlayerEntity player){
        return new GameDetailResponseDTO.PlayerResponseDTO(
                player.id,
                player.name,
                player.lives,
                player.posx,
                player.posy
        );
    }
}
