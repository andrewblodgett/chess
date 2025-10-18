package service;

import chess.ChessGame;
import dataaccess.DataAccess;
import datamodel.GameData;

import java.util.Collection;

public class GameService {

    private final DataAccess dataAccess;
    private int gameIDCounter;

    public GameService(DataAccess dataAccess) {
        this.dataAccess = dataAccess;
        gameIDCounter = 1;
    }

    public GameData createGame(String authToken, String gameName) {
        var auth = dataAccess.getAuth(authToken);
        var newGame = new GameData(gameIDCounter++, null, null, gameName, new ChessGame());
        dataAccess.createGame(newGame);
        return newGame;
    }

    public void joinGame(String authToken, int gameID, String playerColor) {
        var auth = dataAccess.getAuth(authToken);
        var game = dataAccess.getGame(gameID);
        var whiteUsername = game.whiteUsername();
        var blackUsername = game.blackUsername();
        if (playerColor.equals("WHITE")) {
            if (whiteUsername != null) {
                throw new ColorAlreadyTakenException("White is already taken in this game");
            } else {
                whiteUsername = auth.username();
            }
        } else {
            if (blackUsername != null) {
                throw new ColorAlreadyTakenException("Black is already taken in this game");
            } else {
                blackUsername = auth.username();
            }
        }
        dataAccess.updateGame(new GameData(gameID, whiteUsername, blackUsername, game.gameName(), game.game()));

    }

    public Collection<GameData> listGames(String authToken) {
        var auth = dataAccess.getAuth(authToken);
        return dataAccess.listGames();
    }
}
