package server.websocket;

import chess.ChessGame;
import chess.ChessMove;
import chess.ChessPosition;
import com.google.gson.Gson;
import io.javalin.websocket.*;
import org.jetbrains.annotations.NotNull;
import service.GameService;
import websocket.commands.UserGameCommand;
import websocket.messages.ErrorServerMessage;
import websocket.messages.ServerMessage;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static websocket.commands.UserGameCommand.CommandType.*;

public class WebSocketHandler implements WsConnectHandler, WsMessageHandler, WsCloseHandler {
    private final ConcurrentHashMap<Integer, ConnectionManager> connections = new ConcurrentHashMap<>();
    private final GameService gameService;

    public WebSocketHandler(GameService gs) {
        gameService = gs;
    }

    private UserGameCommand deserializeCommand(String message) {
        return new Gson().fromJson(message, UserGameCommand.class);
    }

    @Override
    public void handleConnect(WsConnectContext ctx) {
        System.out.println("Websocket connected!");
        ctx.enableAutomaticPings();
    }

    @Override
    public void handleClose(@NotNull WsCloseContext ctx) throws Exception {
        System.out.println("Websocket closed");
    }

    @Override
    public void handleMessage(@NotNull WsMessageContext ctx) throws Exception {
        try {
            var command = deserializeCommand(ctx.message());
            switch (command.getCommandType()) {
                case CONNECT:
                    connect(ctx, command);
                    break;
                case MAKE_MOVE:
                    makeMove(ctx, command);
                    break;
                case RESIGN:
                    resign(ctx, command);
                    break;
                case LEAVE:
                    leave(ctx, command);
                    break;
            }
        } catch (Exception e) {
            ctx.session.getRemote().sendString(new Gson().toJson(new ErrorServerMessage(e.toString())));
            System.out.println("Error: " + e.toString());
        }
    }

    private void connect(WsMessageContext ctx, UserGameCommand command) throws IOException {
        var username = gameService.getUsername(command.getAuthToken());
        var gameData = gameService.getGame(command.getAuthToken(), command.getGameID());
        ctx.session.getRemote().sendString(new Gson().toJson(new ServerMessage(
                ServerMessage.ServerMessageType.LOAD_GAME, gameData.game())));
        if (!connections.containsKey(command.getGameID())) {
            connections.put(command.getGameID(), new ConnectionManager());
        }
        var currentConnection = connections.get(command.getGameID());
        currentConnection.broadcast(ctx.session, new ServerMessage(ServerMessage.ServerMessageType.NOTIFICATION, username + " connected to the game with " + gameData.whiteUsername() + " as white  and " + gameData.blackUsername() + " as black."));
        currentConnection.add(ctx.session);
    }

    private void makeMove(WsMessageContext ctx, UserGameCommand command) throws Exception {
        var gameData = gameService.makeMove(command.getAuthToken(), command.getGameID(), command.getMove());
        var currentConnection = connections.get(command.getGameID());
        var username = gameData.game().isWhitesTurn() ? gameData.blackUsername() : gameData.whiteUsername();

        currentConnection.broadcast(null, new ServerMessage(ServerMessage.ServerMessageType.LOAD_GAME, gameData.game()));
        Map<Integer, String> coordMap = Map.of(1, "A", 2, "B",
                3, "C",
                4, "D",
                5, "E",
                6, "F",
                7, "G",
                8, "H");
        var start = command.getMove().getStartPosition();
        var end = command.getMove().getEndPosition();
        String move = coordMap.get(start.getColumn()) + start.getRow() + " to " + coordMap.get(end.getColumn()) + end.getRow();
        currentConnection.broadcast(ctx.session, new ServerMessage(ServerMessage.ServerMessageType.NOTIFICATION, username + " moved a piece from " + move));

        if (gameData.game().isOver()) {
            if (gameData.game().getWinner() == ChessGame.TeamColor.BLACK) {
                currentConnection.broadcast(null, new ServerMessage(ServerMessage.ServerMessageType.NOTIFICATION, "The game is over. " + gameData.blackUsername() + " is the winner!"));
            } else if (gameData.game().getWinner() == ChessGame.TeamColor.WHITE) {
                currentConnection.broadcast(null, new ServerMessage(ServerMessage.ServerMessageType.NOTIFICATION, "The game is over. " + gameData.whiteUsername() + " is the winner!"));
            } else {
                currentConnection.broadcast(null, new ServerMessage(ServerMessage.ServerMessageType.NOTIFICATION, "The game is over. The game ended in a stalemate. "));
            }
        }
    }

    private void resign(WsMessageContext ctx, UserGameCommand command) throws Exception {
        var gameData = gameService.resign(command.getAuthToken(), command.getGameID());
        var currentConnection = connections.get(command.getGameID());
        var username = gameData.game().getWinner() == ChessGame.TeamColor.BLACK ? gameData.blackUsername() : gameData.whiteUsername();


        currentConnection.broadcast(null, new ServerMessage(ServerMessage.ServerMessageType.NOTIFICATION, username + " resigned. "));
    }

    private void leave(WsMessageContext ctx, UserGameCommand command) throws Exception {
        var username = gameService.leaveGame(command.getAuthToken(), command.getGameID());
        var currentConnection = connections.get(command.getGameID());

        currentConnection.remove(ctx.session);
        currentConnection.broadcast(null, new ServerMessage(ServerMessage.ServerMessageType.NOTIFICATION, username + " has left the game"));
    }
}