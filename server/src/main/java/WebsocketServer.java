import io.javalin.Javalin;
import websocket.commands.UserGameCommand;
import websocket.messages.ServerMessage;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
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

                        var byteOutputStream = new ByteArrayOutputStream();
                        var objectOutputStream = new ObjectOutputStream(byteOutputStream);
                        objectOutputStream.writeObject(new ServerMessage(ServerMessage.ServerMessageType.NOTIFICATION));
                        byte[] byteArray = byteOutputStream.toByteArray();
                        ByteBuffer byteBuffer = ByteBuffer.wrap(byteArray);
                        ctx.send(byteBuffer);
                    });
                    ws.onClose(_ -> System.out.println("Websocket closed"));
                })
                .start(8090);
    }

}