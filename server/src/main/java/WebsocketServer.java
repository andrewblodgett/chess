import chess.ChessMove;
import chess.ChessPosition;
import com.google.gson.Gson;
import io.javalin.Javalin;
import io.javalin.websocket.WsBinaryMessageContext;
import websocket.commands.UserGameCommand;
import websocket.messages.ServerMessage;

import java.io.*;
import java.nio.ByteBuffer;
import java.util.Map;

import static websocket.commands.UserGameCommand.CommandType.*;

public class WebsocketServer {
    public static void main(String[] args) {
        var c = new WebsocketServer().deserializeCommand("{\n" +
                "  \"commandType\": \"MAKE_MOVE\",\n" +
                "  \"authToken\": \"tokengoeshere\",\n" +
                "  \"gameID\": \"337\",\n" +
                "  \"move\": { \"start\": { \"row\": 3, \"col\": 3 }, \"end\": { \"row\": 5, \"col\": 5 } }\n" +
                "}");
        System.out.println(c.getCommandType().toString() + c.getAuthToken() + c.getGameID().toString());

        Javalin.create()
                .ws("/ws", ws -> {
                    ws.onConnect(ctx -> {
                        ctx.enableAutomaticPings();
                        System.out.println("Websocket connected");
                    });
                    ws.onMessage(ctx -> {
                        System.out.println(ctx.message());

                        switch (new WebsocketServer().deserializeCommand(ctx.message()).getCommandType()) {
                            case CONNECT:
                                break;
                            case LEAVE:
                                break;
                            case RESIGN:
                                break;
                            case MAKE_MOVE:
                                break;

                        }

                    });
                    ws.onClose(_ -> System.out.println("Websocket closed"));
                })
                .start(8090);
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
        command = new UserGameCommand(commandType, map.get("authToken").toString(), Integer.parseInt(map.get("gameID").toString()), move);
        return command;
    }
}