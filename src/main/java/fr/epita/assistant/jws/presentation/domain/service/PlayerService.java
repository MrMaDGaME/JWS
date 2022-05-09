package fr.epita.assistant.jws.presentation.domain.service;

import fr.epita.assistant.jws.presentation.data.model.GameModel;
import fr.epita.assistant.jws.presentation.data.model.PlayerModel;
import fr.epita.assistant.jws.presentation.data.repository.PlayerRepository;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.transaction.Transactional;
import java.time.LocalDateTime;

@ApplicationScoped
public class PlayerService {
    @Inject PlayerRepository playerRepository;

    @Transactional
    public PlayerModel createPlayerModel(GameModel game, String name, int posx, int posy){
        PlayerModel player = new PlayerModel()
                .withLives(3)
                .withName(name)
                .withPosx(posx)
                .withPosy(posy)
                .withGame_id(game)
                .withLastBomb(LocalDateTime.now())
                .withLastMove(LocalDateTime.now());
        playerRepository.persist(player);
        return player;
    }

    public boolean isPlayerAlive(Long id){
        var player = playerRepository.findById(id);
        if (player == null){
            return false;
        }
        return player.lives > 0;
    }

    public boolean isPlayerHere(Long id, int posx, int posy) {
        var player = playerRepository.findById(id);
        if (player == null){
            return false;
        }
        return player.posx == posx && player.posy == posy;
    }
}
