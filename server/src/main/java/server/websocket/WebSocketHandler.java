package server.websocket;

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
            System.out.println("Incorrect Format " + e.toString());
            throw new RuntimeException(e);
        }
    }

    private void connect(WsMessageContext ctx, UserGameCommand command) throws IOException {
        var gameData = gameService.getGame(command.getAuthToken(), command.getGameID());
        ctx.session.getRemote().sendString(new Gson().toJson(new ServerMessage(
                ServerMessage.ServerMessageType.LOAD_GAME, gameData.game())));
        if (!connections.containsKey(command.getGameID())) {
            connections.put(command.getGameID(), new ConnectionManager());
        }
        var currentConnection = connections.get(command.getGameID());
        currentConnection.broadcast(ctx.session, new ServerMessage(ServerMessage.ServerMessageType.NOTIFICATION, "A player/observer connected to the game. Game with " + gameData.whiteUsername() + " as white  and " + gameData.blackUsername() + " as black."));
        currentConnection.add(ctx.session);
    }

    private void makeMove(WsMessageContext ctx, UserGameCommand command) throws Exception {
        var game = gameService.makeMove(command.getAuthToken(), command.getGameID(), command.getMove());
        var currentConnection = connections.get(command.getGameID());
        var username = game.game().isWhitesTurn() ? game.blackUsername() : game.whiteUsername();

        currentConnection.broadcast(null, new ServerMessage(ServerMessage.ServerMessageType.LOAD_GAME, game.game()));
        currentConnection.broadcast(ctx.session, new ServerMessage(ServerMessage.ServerMessageType.NOTIFICATION, username + " moved a piece"));
    }

    private void resign(WsMessageContext ctx, UserGameCommand command) throws Exception {
        gameService.resign(command.getAuthToken(), command.getGameID());
        var currentConnection = connections.get(command.getGameID());

        currentConnection.broadcast(null, new ServerMessage(ServerMessage.ServerMessageType.NOTIFICATION, "your opponent resigned resigned"));
    }

    private void leave(WsMessageContext ctx, UserGameCommand command) throws Exception {
        gameService.leaveGame(command.getAuthToken(), command.getGameID());
        var currentConnection = connections.get(command.getGameID());

        currentConnection.remove(ctx.session);
        currentConnection.broadcast(null, new ServerMessage(ServerMessage.ServerMessageType.NOTIFICATION, "your opponent left the game"));
    }
}