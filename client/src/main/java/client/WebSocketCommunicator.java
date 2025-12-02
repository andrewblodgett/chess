package client;

import jakarta.websocket.ContainerProvider;
import jakarta.websocket.Endpoint;
import jakarta.websocket.EndpointConfig;
import jakarta.websocket.MessageHandler;
import jakarta.websocket.Session;
import jakarta.websocket.WebSocketContainer;
import websocket.commands.UserGameCommand;
import websocket.messages.ServerMessage;

import java.io.*;
import java.net.URI;
import java.nio.ByteBuffer;

public class WebSocketCommunicator extends Endpoint {
    public Session session;

    static void main(String[] args) {
        try {
            var wsc = new WebSocketCommunicator(8080);
            while (true) {
                wsc.send(new UserGameCommand(UserGameCommand.CommandType.CONNECT, "asdas", 1));

            }

        } catch (Exception e) {
        }
    }

    public WebSocketCommunicator(int port) throws Exception {
        URI uri = new URI("ws://localhost:8080/ws");
        WebSocketContainer container = ContainerProvider.getWebSocketContainer();
        session = container.connectToServer(this, uri);

        this.session.addMessageHandler(new MessageHandler.Whole<byte[]>() {
            public void onMessage(byte[] bytes) {
                try {
                    ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
                    ObjectInputStream ois = new ObjectInputStream(bais);
                    ServerMessage message = (ServerMessage) ois.readObject();
                    System.out.println(message.getServerMessageType().toString());
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
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
