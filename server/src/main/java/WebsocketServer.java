import io.javalin.Javalin;
import io.javalin.websocket.WsBinaryMessageContext;
import websocket.commands.UserGameCommand;
import websocket.messages.ServerMessage;

import java.io.*;
import java.nio.ByteBuffer;

public class WebsocketServer {
    public static void main(String[] args) {
        Javalin.create()
                .ws("/ws", ws -> {
                    ws.onConnect(ctx -> {
                        ctx.enableAutomaticPings();
                        System.out.println("Websocket connected");
                    });
                    ws.onMessage(ctx -> {
                        System.out.println(ctx.message());
                    });
                    ws.onBinaryMessage(ctx -> {
                        ByteArrayInputStream bais = new ByteArrayInputStream(ctx.data());
                        ObjectInputStream ois = new ObjectInputStream(bais);
                        UserGameCommand command = (UserGameCommand) ois.readObject();
                        System.out.println(command.getAuthToken());
                        switch (command.getCommandType()) {
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

    private void serializeAndSend(WsBinaryMessageContext ctx, ServerMessage message) throws IOException {
        var byteOutputStream = new ByteArrayOutputStream();
        var objectOutputStream = new ObjectOutputStream(byteOutputStream);
        objectOutputStream.writeObject(message);
        byte[] byteArray = byteOutputStream.toByteArray();
        ByteBuffer byteBuffer = ByteBuffer.wrap(byteArray);
        ctx.send(byteBuffer);
    }

}