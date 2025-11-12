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
    public void basicGetTest() {
        assertThrows(Exception.class, () -> {
            facade.get("gobbledygook", "");
        });
    }

    @Test
    public void listGamesTest() {
        assertDoesNotThrow(() -> {
            var games = facade.listGames("secret");
            System.out.println(games);
        });
    }

}
