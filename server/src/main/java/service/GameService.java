package service;

import chess.ChessGame;
import chess.ChessMove;
import chess.ChessPosition;
import dataaccess.DataAccess;
import datamodel.GameData;

import java.util.Collection;
import java.util.Objects;

public class GameService {

    private final DataAccess dataAccess;
    private int gameIDCounter;

    public GameService(DataAccess dataAccess) {
        this.dataAccess = dataAccess;
        gameIDCounter = 0;
    }

    public String getUsername(String authToken) {
        var auth = dataAccess.getAuth(authToken);
        if (auth == null) {
            throw new UnauthorizedException("Not a recognized auth token");
        }
        return auth.username();
    }

    public GameData createGame(String authToken, String gameName) {
        var auth = dataAccess.getAuth(authToken);
        if (auth == null) {
            throw new UnauthorizedException("Not a recognized auth token");
        }
        gameIDCounter++;
        var newGame = new GameData(gameIDCounter, null, null, gameName, new ChessGame());
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

    public String leaveGame(String authToken, int gameID) throws Exception {
        var auth = dataAccess.getAuth(authToken);
        if (auth == null) {
            throw new UnauthorizedException("Not a recognized auth token");
        }
        var gameData = dataAccess.getGame(gameID);
        if (gameData == null) {
            throw new RuntimeException("Not a valid game ID");
        }
        String usernameOfTheQuitter = auth.username();
        var whiteUsername = gameData.whiteUsername();
        var blackUsername = gameData.blackUsername();
        if (Objects.equals(gameData.whiteUsername(), auth.username())) {
            whiteUsername = null;
        } else if (Objects.equals(gameData.blackUsername(), auth.username())) {
            blackUsername = null;
        }
        var updatedGame = new GameData(gameID, whiteUsername, blackUsername, gameData.gameName(), gameData.game());
        dataAccess.updateGame(updatedGame);
        return usernameOfTheQuitter;
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
        var gameData = dataAccess.getGame(gameID);
        if (gameData == null) {
            throw new RuntimeException("Not a valid game ID");
        }
        if ((Objects.equals(gameData.whiteUsername(), auth.username()) && gameData.game().isWhitesTurn()) ||
                (Objects.equals(gameData.blackUsername(), auth.username()) && !gameData.game().isWhitesTurn())) {
            var moved = gameData.game();
            if (moved.isOver()) {
                throw new Exception("This game has already finished, no more moves can be made");
            }
            moved.makeMove(move);
//            moved.isInCheckmate(ChessGame.TeamColor.WHITE);
//            moved.isInCheckmate(ChessGame.TeamColor.BLACK);
            var updatedGame = new GameData(gameID, gameData.whiteUsername(), gameData.blackUsername(), gameData.gameName(), moved);
            dataAccess.updateGame(updatedGame);
            return updatedGame;
        } else {
            throw new Exception("You can't move the opponents pieces");
        }

    }


    public GameData resign(String authToken, int gameID) throws Exception {
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
        return updatedGame;
    }
}
