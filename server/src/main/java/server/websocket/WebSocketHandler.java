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

import static websocket.commands.UserGameCommand.CommandType.*;

public class WebSocketHandler implements WsConnectHandler, WsMessageHandler, WsCloseHandler {
    private final ConnectionManager connections = new ConnectionManager();
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
            Map<String, Double> start = new Gson().fromJson(positions.get("start").toString(), Map.class);
            Map<String, Double> end = new Gson().fromJson(positions.get("end").toString(), Map.class);
            move = new ChessMove(new ChessPosition((int) Math.round(start.get("row")), (int) Math.round(start.get("col"))), new ChessPosition((int) Math.round(end.get("row")), (int) Math.round(end.get("col"))), null);
        }
        command = new UserGameCommand(commandType, map.get("authToken").toString(), (int) Math.round(Double.parseDouble(map.get("gameID").toString())), move);
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
            if (command.getCommandType().equals(CONNECT)) {
                connect(ctx, command);

            }
        } catch (Exception e) {
            ctx.session.getRemote().sendString(new Gson().toJson(new ErrorServerMessage(e.toString())));
            System.out.println("Incorrect Format");
            throw new RuntimeException(e);
        }
    }

    private void connect(WsMessageContext ctx, UserGameCommand command) throws IOException {
        ctx.session.getRemote().sendString(new Gson().toJson(new ServerMessage(ServerMessage.ServerMessageType.LOAD_GAME, gameService.getGame(command.getAuthToken(), command.getGameID()).game())));
        connections.broadcast(null, new ServerMessage(ServerMessage.ServerMessageType.NOTIFICATION, "gamers"));
        connections.add(ctx.session);
    }
}