package client;

import chess.ChessGame;
import org.junit.jupiter.api.*;
import server.Server;

import static org.junit.jupiter.api.Assertions.*;

public class ServerFacadeTests {

    private static Server server;
    private static ServerFacade facade;

    @BeforeAll
    public static void init() {
        server = new Server();
        var port = server.run(0);
        facade = new ServerFacade(port);
        System.out.println("Started test HTTP server on " + port);
    }

    @AfterAll
    static void stopServer() {
        server.stop();
    }

    @BeforeAll
    public static void clear() {
        assertDoesNotThrow(() -> {
            facade.clear();
        });
    }

    @Test
    public void registerTest() {
        assertDoesNotThrow(() -> {
            var auth = facade.register("123", "123", "123");
            System.out.println(auth);
            facade.logout(auth);
        });
    }

    @Test
    public void registerDuplicateTest() {
        assertDoesNotThrow(() -> {
            var auth = facade.register("2212", "2212", "2212");
            System.out.println(auth);
        });
        assertThrows(Exception.class, () -> {
            var auth = facade.register("2212", "2212", "2212");
            System.out.println(auth);
        });
    }

    @Test
    public void logoutTest() {
        assertDoesNotThrow(() -> {
            var auth = facade.register("333", "333", "333");
            facade.logout(auth);
            System.out.println(auth);
        });
    }

    @Test
    public void logoutInvalidAuthTest() {
        assertThrows(Exception.class, () -> {
            var auth = "haha get pranked";
            facade.logout(auth);
            System.out.println(auth);
        });
    }

    @Test
    public void loginTest() {
        assertDoesNotThrow(() -> {
            var auth = facade.register("444", "444", "444");
            System.out.println(auth);
            facade.logout(auth);
            auth = facade.login("444", "444");
            System.out.println(auth);
        });
    }

    @Test
    public void loginBadPasswordTest() {
        assertThrows(Exception.class, () -> {
            var auth = facade.register("gg", "444", "444");
            facade.logout(auth);
            auth = facade.login("gg", "gg");
            System.out.println(auth);
        });
    }

    @Test
    public void createGameTest() {
        assertDoesNotThrow(() -> {
            var games = facade.createGame(facade.register("555", "555", "555"), "my game");
            System.out.println(games);
        });
    }

    @Test
    public void createGameInvalidAuthTest() {
        assertThrows(Exception.class, () -> {
            var auth = "haha get pranked";
            facade.createGame(auth, auth);
        });
    }

    @Test
    public void listGamesTest() {
        assertDoesNotThrow(() -> {
            var games = facade.listGames(facade.register("222", "222", "222"));
            System.out.println(games);
        });
    }

    @Test
    public void listGamesInvalidAuthTest() {
        assertThrows(Exception.class, () -> {
            var auth = "haha get pranked";
            facade.listGames(auth);
        });
    }

    @Test
    public void joinGameTest() {
        assertDoesNotThrow(() -> {
            var auth = facade.register("666", "666", "666");
            var gameID = facade.createGame(auth, "my game");
            System.out.println(gameID);
            facade.joinGame(auth, gameID, ChessGame.TeamColor.WHITE);
        });
    }

    @Test
    public void joinGameInvalidIDTest() {
        assertThrows(Exception.class, () -> {
            var auth = facade.register("999", "5599", "9");
            facade.joinGame(auth, 324324L, ChessGame.TeamColor.WHITE);
        });
    }

}
