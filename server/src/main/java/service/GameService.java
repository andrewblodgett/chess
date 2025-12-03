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
        if (auth == null) {
            throw new UnauthorizedException("Not a recognized auth token");
        }
        var newGame = new GameData(gameIDCounter++, null, null, gameName, new ChessGame());
        dataAccess.createGame(newGame);
        return newGame;
    }

    public void joinGame(String authToken, int gameID, String playerColor) {
        var auth = dataAccess.getAuth(authToken);
        if (auth == null) {
            throw new UnauthorizedException("Not a recognized auth token");
        }
        var game = dataAccess.getGame(gameID);
        if (game == null) {
            throw new RuntimeException("Not a valid game ID");
        }
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
        if (auth == null) {
            throw new UnauthorizedException("Not a recognized auth token");
        }
        return dataAccess.listGames();
    }

    public GameData getGame(String authToken, int gameID) {
        var auth = dataAccess.getAuth(authToken);
        if (auth == null) {
            throw new UnauthorizedException("Not a recognized auth token");
        }
        var game = dataAccess.getGame(gameID);
        if (game == null) {
            throw new RuntimeException("Not a valid game ID");
        }
        return game;
    }
}
