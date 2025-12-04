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
        UserGameCommand command = null;
        ChessMove move = null;
        UserGameCommand.CommandType commandType = null;
        Map map = new Gson().fromJson(message, Map.class);
        if (map.get("commandType").equals("CONNECT")) {
            commandType = CONNECT;
        } else if (map.get("commandType").equals("LEAVE")) {
            commandType = LEAVE;
        } else if (map.get("commandType").equals("RESIGN")) {
            commandType = RESIGN;
        } else if (map.get("commandType").equals("MAKE_MOVE")) {
            commandType = MAKE_MOVE;
            Map positions = new Gson().fromJson(map.get("move").toString(), Map.class);
            Map<String, Double> start = new Gson().fromJson(positions.get("startPosition").toString(), Map.class);
            Map<String, Double> end = new Gson().fromJson(positions.get("endPosition").toString(), Map.class);
            move = new ChessMove(new ChessPosition((int) Math.round(start.get("row")), (int) Math.round(start.get("col"))),
                    new ChessPosition((int) Math.round(end.get("row")), (int) Math.round(end.get("col"))), null);
        }
        command = new UserGameCommand(commandType, map.get("authToken").toString(),
                (int) Math.round(Double.parseDouble(map.get("gameID").toString())), move);
        return command;
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
        currentConnection.broadcast(ctx.session, new ServerMessage(ServerMessage.ServerMessageType.NOTIFICATION, username + " moved a piece"));

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