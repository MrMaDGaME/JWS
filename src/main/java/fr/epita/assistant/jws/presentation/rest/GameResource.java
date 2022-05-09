package fr.epita.assistant.jws.presentation.rest;


import fr.epita.assistant.jws.presentation.converter.GameEntityToGameDetailResponseDTO;
import fr.epita.assistant.jws.presentation.domain.entity.GameEntity;
import fr.epita.assistant.jws.presentation.domain.service.GameService;
import fr.epita.assistant.jws.presentation.domain.service.PlayerService;
import fr.epita.assistant.jws.presentation.rest.request.PositionRequestDTO;
import fr.epita.assistant.jws.presentation.rest.request.GameRequestDTO;
import fr.epita.assistant.jws.presentation.rest.response.GameResponseDTO;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import javax.inject.Inject;
import javax.ws.rs.*;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;
import java.util.stream.Collectors;

@Path("/games")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class GameResource {
    @Inject GameService gameService;
    @Inject PlayerService playerService;
    @Inject GameEntityToGameDetailResponseDTO converter;

    @ConfigProperty(name = "JWS_MAP_PATH") String mapPath;
    @ConfigProperty(name = "JWS_TICK_DURATION") int tickDuration;
    @ConfigProperty(name = "JWS_DELAY_MOVEMENT") int delayMovement;
    @ConfigProperty(name = "JWS_DELAY_BOMB") int delayBomb;

    @GET @Path("/")
    public List<GameResponseDTO> getAllGames() {
        List<GameEntity> games = gameService.getAll();
        return games.stream().map(game -> new GameResponseDTO(
                game.id,
                game.players.size(),
                game.state))
                .collect(Collectors.toList());
    }

    @POST @Path("/")
    public Response createGame(GameRequestDTO request){
        if (request == null || request.name == null){
            return Response.status(400).build();
        }
        var GameEntity = gameService.createGame(request.name, mapPath);
        return Response.ok(converter.convert(GameEntity)).build();
    }

    @GET @Path("/{GameId}")
    public Response GetGame(@PathParam("GameId") Long id){
        if (id == null)
            return Response.status(400).build();
        var game = gameService.getGame(id);
        if (game == null){
            return Response.status(404).build();
        }
        return Response.ok(converter.convert(game)).build();
    }

    @POST @Path("/{GameId}")
    public Response joinGame(@PathParam("GameId") Long id, GameRequestDTO request){
        if (request == null || request.name == null || id == null){
            return Response.status(400).build();
        }
        var game = gameService.addPlayer(id, request.name);
        if (game == null) {
            return Response.status(404).build();
        }
        return Response.ok(converter.convert(game)).build();
    }

    @PATCH @Path("/{GameId}/start")
    public Response startGame(@PathParam("GameId") Long id){
        if (id == null){
            return Response.status(400).build();
        }
        var game = gameService.startGame(id);
        if (game == null){
            return Response.status(404).build();
        }
        return Response.ok(converter.convert(game)).build();
    }

    @POST @Path("/{GameId}/players/{PlayerId}/bomb")
    public Response putBomb(@PathParam("GameId") Long gameId, @PathParam("PlayerId") Long playerId, PositionRequestDTO request){
        if (request == null || playerId == null || gameId == null){
            System.out.println("400");
            return Response.status(400).build();
        }
        if (!gameService.gameAndPlayerExists(gameId, playerId)){
            System.out.println("404");
            return Response.status(404).build();
        }
        if (!gameService.isGameRunning(gameId) || !playerService.isPlayerAlive(playerId) || !playerService.isPlayerHere(playerId, request.posX, request.posY)){
            System.out.println("400");
            return Response.status(400).build();
        }
        var game = gameService.putBomb(gameId, playerId, tickDuration, delayBomb);
        if (game == null) {
            System.out.println("429");
            return Response.status(429).build();
        }
        return Response.ok(converter.convert(game)).build();
    }

    @POST @Path("/{GameId}/players/{PlayerId}/move")
    public Response movePlayer(@PathParam("GameId") Long gameId, @PathParam("PlayerId") Long playerId, PositionRequestDTO request){
        if (request == null || playerId == null || gameId == null){
            System.out.println("400");
            return Response.status(400).build();
        }
        if (!gameService.gameAndPlayerExists(gameId, playerId)){
            System.out.println("404");
            return Response.status(404).build();
        }
        if (!gameService.canPlayerMove(gameId, playerId, request.posX, request.posY)){
            System.out.println("400");
            return Response.status(400).build();
        }
        var game = gameService.movePlayer(gameId, playerId, request.posX, request.posY, tickDuration, delayMovement);
        if (game == null) {
            System.out.println("429");
            return Response.status(429).build();
        }
        return Response.ok(converter.convert(game)).build();
    }
}