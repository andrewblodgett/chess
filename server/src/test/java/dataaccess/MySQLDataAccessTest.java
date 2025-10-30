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
    void createNullUser() {
        assertThrows(Exception.class, () -> {
            dataAccess.createUser(null);
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
    void getFakeUser() {
        assertNull(dataAccess.getUser("sdfkjdshkj"));
    }

    @Test
    void createGame() {
        assertDoesNotThrow(() -> {
            dataAccess.createGame(new GameData(1, "w", "b", "game", new ChessGame()));
        });
    }

    @Test
    void createNullGame() {
        assertThrows(Exception.class, () -> {
            dataAccess.createGame(null);
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
    void getFakeGame() {
        assertNull(dataAccess.getGame(5));
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
    void updateBadGame() {
        assertThrows(Exception.class, () -> {
            dataAccess.updateGame(null);
        });
    }

    @Test
    void createAuth() {
        assertDoesNotThrow(() -> {
            dataAccess.createAuth(new AuthData("akdaskjfhakjfash", "bob"));
        });
    }

    @Test
    void createBadAuth() {
        assertThrows(Exception.class, () -> {
            dataAccess.createAuth(null);
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
    void deleteFakeAuth() {
        assertDoesNotThrow(() -> {
            dataAccess.deleteAuth(null);
        });
    }

    @Test
    void getAuth() {
        assertDoesNotThrow(() -> {
            dataAccess.createAuth(new AuthData("akdaskjfhakjfash", "bob"));
            dataAccess.getAuth("akdaskjfhakjfash");
        });
    }

    @Test
    void getFakeAuth() {
        assertNull(dataAccess.getAuth("akdaskjfhakjfash"));
    }
}