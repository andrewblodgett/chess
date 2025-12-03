package service;

import chess.ChessGame;
import chess.ChessMove;
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

    public GameData makeMove(String authToken, int gameID, ChessMove move) throws Exception {
        var auth = dataAccess.getAuth(authToken);
        if (auth == null) {
            throw new UnauthorizedException("Not a recognized auth token");
        }
        var game = dataAccess.getGame(gameID);
        if (game == null) {
            throw new RuntimeException("Not a valid game ID");
        }
        if ((game.whiteUsername().equals(auth.username()) && game.game().isWhitesTurn()) || (game.blackUsername().equals(auth.username()) && !game.game().isWhitesTurn())) {
            var moved = game.game();
            if (moved.isOver()) {
                throw new Exception("This game has already finished, no more moves can be made");
            }
            moved.makeMove(move);
            var updatedGame = new GameData(gameID, game.whiteUsername(), game.blackUsername(), game.gameName(), moved);
            dataAccess.updateGame(updatedGame);
            return updatedGame;
        } else {
            throw new Exception("You can't move the opponents pieces");
        }

    }


    public void resign(String authToken, int gameID) throws Exception {
        var auth = dataAccess.getAuth(authToken);
        if (auth == null) {
            throw new UnauthorizedException("Not a recognized auth token");
        }
        var gameData = dataAccess.getGame(gameID);
        if (gameData == null) {
            throw new RuntimeException("Not a valid game ID");
        }
        var game = gameData.game();
        if (game.isOver()) {
            throw new Exception("This game has already finished, you can't resign");
        }
        if (gameData.whiteUsername().equals(auth.username())) {
            game.resign(ChessGame.TeamColor.WHITE);
        } else if (gameData.blackUsername().equals(auth.username())) {
            game.resign(ChessGame.TeamColor.BLACK);
        } else {
            throw new Exception("umm observers cant resign");
        }
        var updatedGame = new GameData(gameID, gameData.whiteUsername(), gameData.blackUsername(), gameData.gameName(), game);
        dataAccess.updateGame(updatedGame);
    }
}
