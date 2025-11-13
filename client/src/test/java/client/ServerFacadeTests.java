package client;

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

    @Test
    public void clear() {
        assertDoesNotThrow(() -> {
            facade.clear();
        });
    }

    @Test
    public void basicGetTest() {
        assertThrows(Exception.class, () -> {
            facade.get("gobbledygook", "");
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
    public void logoutTest() {
        assertDoesNotThrow(() -> {
            var auth = facade.register("333", "333", "333");
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
    public void listGamesTest() {
        assertDoesNotThrow(() -> {
            var games = facade.listGames(facade.register("222", "222", "222"));
            System.out.println(games);
        });
    }

}
