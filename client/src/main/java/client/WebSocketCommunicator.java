package client;

import jakarta.websocket.ContainerProvider;
import jakarta.websocket.Endpoint;
import jakarta.websocket.EndpointConfig;
import jakarta.websocket.MessageHandler;
import jakarta.websocket.Session;
import jakarta.websocket.WebSocketContainer;
import websocket.commands.UserGameCommand;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.URI;
import java.nio.ByteBuffer;
import java.util.Scanner;

public class WebSocketCommunicator extends Endpoint {
    public Session session;

    static void main(String[] args) {
        try {
            var wsc = new WebSocketCommunicator(8080);
            Scanner scanner = new Scanner(System.in);

            System.out.println("Enter a message you want to echo:");
            wsc.send(new UserGameCommand(UserGameCommand.CommandType.CONNECT, "asdas", 1));

        } catch (Exception e) {
        }
    }

    public WebSocketCommunicator(int port) throws Exception {
        URI uri = new URI("ws://localhost:8080/ws");
        WebSocketContainer container = ContainerProvider.getWebSocketContainer();
        session = container.connectToServer(this, uri);

        this.session.addMessageHandler(new MessageHandler.Whole<String>() {
            public void onMessage(String message) {
                System.out.println(message);
                System.out.println("\nEnter another message you want to echo:");
            }
        });
    }

    public void send(UserGameCommand command) throws IOException {
        var byteOutputStream = new ByteArrayOutputStream();
        var objectOutputStream = new ObjectOutputStream(byteOutputStream);
        objectOutputStream.writeObject(command);
        byte[] byteArray = byteOutputStream.toByteArray();
        ByteBuffer byteBuffer = ByteBuffer.wrap(byteArray);
        session.getBasicRemote().sendBinary(byteBuffer);
    }

    @Override
    public void onOpen(Session session, EndpointConfig endpointConfig) {
    }
}
