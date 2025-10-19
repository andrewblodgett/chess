package service;

import dataaccess.DataAccess;
import dataaccess.MemoryDataAccess;
import datamodel.AuthData;
import datamodel.GameData;
import datamodel.UserData;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ServiceTest {
    DataAccess dataAccess = new MemoryDataAccess();
    GameService gameService = new GameService(dataAccess);
    UserService userService = new UserService(dataAccess);
    AuthData auth = userService.register(new UserData("jimothy", "password123", "1@1.1"));
    GameData game;

    @Test
    void registerAUser() {
        assertNotNull(auth);
    }

    @Test
    void registerFail() {
        assertThrows(Exception.class, () -> {
            userService.register(new UserData("jimothy", "password123", "1@1.1"));
        });
    }

    @Test
    void createGame() {
        assertDoesNotThrow(() -> {
            game = gameService.createGame(auth.authToken(), "game1");
        });
    }

    @Test
    void createGameBadAuth() {
        assertThrows(Exception.class, () -> {
            gameService.createGame("hehe", "game1");
        });
    }


    @Test
    void joinGame() {
        assertDoesNotThrow(() -> {
            var game = gameService.createGame(auth.authToken(), "game2");
            gameService.joinGame(auth.authToken(), game.gameID(), "WHITE");
        });
    }

    @Test
    void joinGameAlreadyTaken() {
        var game = gameService.createGame(auth.authToken(), "game3");
        gameService.joinGame(auth.authToken(), game.gameID(), "WHITE");
        assertThrows(Exception.class, () -> {
            gameService.joinGame(auth.authToken(), game.gameID(), "WHITE");
        });
    }

    @Test
    void listGames() {
        var game = gameService.createGame(auth.authToken(), "game4");
        var game2 = gameService.createGame(auth.authToken(), "game4=5");
        assertDoesNotThrow(() -> {
            gameService.listGames(auth.authToken());
        });
    }

    @Test
    void listGamesBadAuth() {
        var game = gameService.createGame(auth.authToken(), "game4");
        var game2 = gameService.createGame(auth.authToken(), "game4=5");
        assertThrows(Exception.class, () -> {
            gameService.listGames("badAuth");
        });
    }


    @Test
    void loginFail() {
        userService.logout(auth.authToken());
        assertThrows(Exception.class, () -> {
            auth = userService.login(new UserData(auth.username(), "wrong password", "1@1.1"));
        });
    }

    @Test
    void loginSuccess() {
        assertDoesNotThrow(() -> {
            auth = userService.login(new UserData(auth.username(), "password123", "1@1.1"));
        });
    }

    @Test
    void logout() {
        assertDoesNotThrow(() -> {
            userService.logout(auth.authToken());
        });
    }

    @Test
    void logoutBadAuth() {
        assertThrows(Exception.class, () -> {
            userService.logout("badAuth");
        });
    }

    @Test
    void clear() {
        assertDoesNotThrow(() -> {
            userService.clear();
        });
    }

    @Test
    void multipleClears() {
        assertDoesNotThrow(() -> {
            userService.clear();
        });
        assertDoesNotThrow(() -> {
            userService.clear();
        });
        assertDoesNotThrow(() -> {
            userService.clear();
        });
    }
}