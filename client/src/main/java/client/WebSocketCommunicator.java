package client;

import com.google.gson.Gson;
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

public class WebSocketCommunicator extends Endpoint {
    public Session session;
    private ServerMessageObserver observer;

    public WebSocketCommunicator(String url) throws Exception {
        url = url.replace("http", "ws");
        URI uri = new URI(url + "/ws");
        WebSocketContainer container = ContainerProvider.getWebSocketContainer();
        session = container.connectToServer(this, uri);

        this.session.addMessageHandler(new MessageHandler.Whole<String>() {
            @Override
            public void onMessage(String message) {
                try {
                    ServerMessage msg = new Gson().fromJson(message, ServerMessage.class);
                    observer.notify(msg);
                } catch (Exception e) {
                    System.out.println(e);
                    throw new RuntimeException(e);
                }
            }
        });
    }

    public void send(UserGameCommand command) throws IOException {
        var cmd = new Gson().toJson(command);
        session.getBasicRemote().sendText(cmd);
    }

    @Override
    public void onOpen(Session session, EndpointConfig endpointConfig) {
    }

    public void addObserver(ServerMessageObserver observer) {
        this.observer = observer;
    }
}
