package fr.epita.assistant.jws.presentation.domain.service;

import fr.epita.assistant.jws.presentation.converter.GameModelToGameEntityConverter;
import fr.epita.assistant.jws.presentation.converter.RleCoder;
import fr.epita.assistant.jws.presentation.data.model.GameModel;
import fr.epita.assistant.jws.presentation.data.model.PlayerModel;
import fr.epita.assistant.jws.presentation.data.repository.GameRepository;
import fr.epita.assistant.jws.presentation.data.repository.PlayerRepository;
import fr.epita.assistant.jws.presentation.domain.entity.GameEntity;
import fr.epita.assistant.jws.presentation.utils.GameState;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.transaction.Transactional;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.StringJoiner;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@ApplicationScoped
public class GameService {
    @Inject GameRepository gameRepository;
    @Inject PlayerRepository playerRepository;
    @Inject GameModelToGameEntityConverter converter;
    @Inject RleCoder coder;
    @Inject PlayerService playerService;

    @Transactional
    public List<GameEntity> getAll(){
        var games = gameRepository.findAll();
        return games.stream().map(game -> converter.convert(game)).collect(Collectors.toList());
    }

    @Transactional
    public GameEntity createGame(String playerName, String mapPath){
        GameModel gameModel = new GameModel()
                .withStartTime(LocalDateTime.now())
                .withState(GameState.STARTING)
                .withMap(coder.rleDecoder(mapPath))
                .withPlayers(new ArrayList<>());
        var player = playerService.createPlayerModel(gameModel, playerName, 1, 1);
        gameModel.players.add(player);
        gameRepository.persist(gameModel);
        return converter.convert(gameModel);
    }

    @Transactional
    public GameEntity getGame(Long id){
        var game = gameRepository.findById(id);
        if (game == null)
            return null;
        return converter.convert(game);
    }

    @Transactional
    public GameEntity addPlayer(Long id, String playerName){
        var game = gameRepository.findById(id);
        if (game == null){
            return null;
        }
        int nbPlayers = game.players.size();
        if (nbPlayers >= 4 || game.state != GameState.STARTING){
            return null;
        }
        int posx;
        int posy;
        if (nbPlayers == 1){
            posx = 15;
            posy = 1;
        }
        else if (nbPlayers == 2){
            posx = 1;
            posy = 13;
        }
        else if (nbPlayers == 3){
            posx = 15;
            posy = 13;
        }
        else
            return null;
        var player = playerService.createPlayerModel(game, playerName, posx, posy);
        game.players.add(player);
        return converter.convert(game);
    }

    @Transactional
    public GameEntity startGame(Long id){
        var game = gameRepository.findById(id);
        if (game == null){
            return null;
        }
        if (game.players.size() > 1)
            game.state = GameState.RUNNING;
        else
            game.state = GameState.FINISHED;
        return converter.convert(game);
    }

    public boolean isGameRunning(Long id){
        var game = gameRepository.findById(id);
        if (game == null){
            return false;
        }
        return game.state == GameState.RUNNING;
    }

    public boolean gameAndPlayerExists(Long gameId, Long playerId){
        var game = gameRepository.findById(gameId);
        if (game == null){
            return false;
        }
        for (PlayerModel player : game.players){
            if (player.id == playerId.longValue()){
                return true;
            }
        }
        return false;
    }

    @Transactional
    public GameEntity putBomb(Long gameId, Long playerId, int tickDuration, int delayBomb){
        var game = gameRepository.findById(gameId);
        if (game == null){
            throw new IllegalStateException();
        }
        PlayerModel player = null;
        for (PlayerModel p : game.players){
            if (p.id == playerId.longValue()){
                player = p;
            }
        }
        if (player == null){
            throw new IllegalStateException();
        }
        if (LocalDateTime.now().minusSeconds(tickDuration * delayBomb / 1000).isBefore(player.lastBomb)){
            return null;
        }
        player.lastBomb = LocalDateTime.now();
        int position = player.posx + 17 * player.posy;
        game.map = new StringBuilder(game.map).replace(position, position + 1, "B").toString();
        Executor exec = CompletableFuture.delayedExecutor(tickDuration * delayBomb, TimeUnit.MILLISECONDS);
        CompletableFuture.runAsync(() -> explodeBomb(gameId, position), exec);
        return converter.convert(game);
    }

    @Transactional
    public void explodeBomb(Long gameId, int position){
        var game = gameRepository.findById(gameId);
        if (game == null) {
            throw new IllegalStateException();
        }
        StringBuilder builder = new StringBuilder(game.map);
        if (game.map.charAt(position + 1) == 'W'){
            builder.setCharAt(position + 1, 'G');
        }
        if (game.map.charAt(position - 1) == 'W'){
            builder.setCharAt(position - 1, 'G');
        }
        if (game.map.charAt(position + 17) == 'W'){
            builder.setCharAt(position + 17, 'G');
        }
        if (game.map.charAt(position - 17) == 'W'){
            builder.setCharAt(position - 17, 'G');
        }
        game.map = builder.replace(position, position + 1, "G").toString();
        int posx = position % 17;
        int posy = position / 17;

        int nbAlive = 0;
        for (PlayerModel player : game.players) {
            if (
                    (player.posx == posx && player.posy == posy + 1)
                    || (player.posx == posx + 1 && player.posy == posy)
                    || (player.posx == posx && player.posy == posy - 1)
                    || (player.posx == posx - 1 && player.posy == posy)
            ){
                player.lives -= 1;
            }
            if (player.lives > 0){
                nbAlive += 1;
            }
        }
        if (nbAlive <= 1){
            game.state = GameState.FINISHED;
        }
    }

    public boolean canPlayerMove(Long gameId, Long playerId, int posx, int posy){
        var game = gameRepository.findById(gameId);
        var player = playerRepository.findById(playerId);
        return game.map.charAt(posx + 17 * posy) == 'G'
                && player.lives > 0
                && game.state == GameState.RUNNING;
    }

    @Transactional
    public GameEntity movePlayer(Long gameId, Long playerId, int posx, int posy, int tickDuration, int delayMovement){
        var game = gameRepository.findById(gameId);
        if (game == null){
            throw new IllegalStateException();
        }
        PlayerModel player = null;
        for (PlayerModel p : game.players){
            if (p.id == playerId.longValue()){
                player = p;
            }
        }
        if (player == null){
            throw new IllegalStateException();
        }
        if (LocalDateTime.now().minusSeconds(tickDuration * delayMovement / 1000).isBefore(player.lastBomb)){
            return null;
        }
        player.posx = posx;
        player.posy = posy;
        return converter.convert(game);
    }
}
