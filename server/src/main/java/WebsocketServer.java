import io.javalin.Javalin;
import websocket.commands.UserGameCommand;

import java.io.ByteArrayInputStream;
import java.io.ObjectInputStream;
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
                    });
                    ws.onClose(_ -> System.out.println("Websocket closed"));
                })
                .start(8080);
    }

}