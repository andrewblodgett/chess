package client;

import chess.ChessGame;
import websocket.commands.UserGameCommand;

public class ServerFacade {

    HTTPCommunicator httpCommunicator;
    WebSocketCommunicator webSocketCommunicator;
    int port;

    public ServerFacade(int port) {
        this.port = port;
        httpCommunicator = new HTTPCommunicator(port);
        try {
            webSocketCommunicator = new WebSocketCommunicator("http://localhost:" + port);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void clear() throws Exception {
        httpCommunicator.clear();
    }

    public String register(String username, String password, String email) throws Exception {
        return httpCommunicator.register(username, password, email);
    }

    public String login(String username, String password) throws Exception {
        return httpCommunicator.login(username, password);
    }

    public void logout(String authToken) throws Exception {
        httpCommunicator.logout(authToken);
    }

    public Long createGame(String authToken, String gameName) throws Exception {
        return httpCommunicator.createGame(authToken, gameName);
    }

    public String listGames(String authToken) throws Exception {
        return httpCommunicator.listGames(authToken);
    }

    public void joinGame(String authToken, Long gameID, ChessGame.TeamColor playerColor) throws Exception {
        httpCommunicator.joinGame(authToken, gameID, playerColor);
        try {
            webSocketCommunicator.send(new UserGameCommand(UserGameCommand.CommandType.CONNECT, authToken, (int) Math.round(gameID)));
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    public void addObserver(ServerMessageObserver client) {
        webSocketCommunicator.addObserver(client);
    }
}