package dataaccess;

import chess.ChessGame;
import datamodel.AuthData;
import datamodel.GameData;
import datamodel.UserData;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashSet;

import static org.junit.jupiter.api.Assertions.*;

class MySQLDataAccessTest {

    private DataAccess dataAccess = new MySQLDataAccess();

    @BeforeEach
    void setUp() {
        dataAccess.clear();
    }

    @Test
    void clear() {
        dataAccess.createGame(new GameData(1, "w", "b", "game", new ChessGame()));
        dataAccess.clear();
        assertEquals(new HashSet<GameData>(), dataAccess.listGames());
    }

    @Test
    void createUser() {
        assertDoesNotThrow(() -> {
            dataAccess.createUser(new UserData("him", "123", "sdfsdfkjsdhfkjsd"));
        });
    }

    @Test
    void getUser() {
        assertDoesNotThrow(() -> {
            dataAccess.createUser(new UserData("him", "123", "sdfsdfkjsdhfkjsd"));
            dataAccess.getUser("him");
        });
    }

    @Test
    void createGame() {
        assertDoesNotThrow(() -> {
            dataAccess.createGame(new GameData(1, "w", "b", "game", new ChessGame()));
        });
    }

    @Test
    void getGame() {
        assertDoesNotThrow(() -> {
            dataAccess.createGame(new GameData(1, "w", "b", "game", new ChessGame()));
            dataAccess.getGame(1);
        });
    }

    @Test
    void listGames() {
        assertDoesNotThrow(() -> {
            dataAccess.createGame(new GameData(1, "w", "b", "game", new ChessGame()));
            dataAccess.createGame(new GameData(2, "w", "b", "game", new ChessGame()));
            dataAccess.listGames();
        });
    }

    @Test
    void updateGame() {
        assertDoesNotThrow(() -> {
            dataAccess.createGame(new GameData(1, "w", "b", "game", new ChessGame()));
            dataAccess.updateGame(new GameData(1, "w", null, "game", new ChessGame()));
        });
    }

    @Test
    void createAuth() {
        assertDoesNotThrow(() -> {
            dataAccess.createAuth(new AuthData("akdaskjfhakjfash", "bob"));
        });
    }

    @Test
    void deleteAuth() {
        assertDoesNotThrow(() -> {
            dataAccess.createAuth(new AuthData("akdaskjfhakjfash", "bob"));
            dataAccess.deleteAuth("akdaskjfhakjfash");
        });
    }

    @Test
    void getAuth() {
        assertDoesNotThrow(() -> {
            dataAccess.createAuth(new AuthData("akdaskjfhakjfash", "bob"));
            dataAccess.getAuth("akdaskjfhakjfash");
        });
    }
}