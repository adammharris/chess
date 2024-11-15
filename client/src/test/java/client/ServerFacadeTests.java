package client;

import model.GameData;
import org.junit.jupiter.api.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import server.Server;

import java.io.IOException;
import static org.junit.jupiter.api.Assertions.*;


public class ServerFacadeTests {

    private static final Logger log = LoggerFactory.getLogger(ServerFacadeTests.class);
    private static Server server;
    private static ServerFacade facade = null;
    private static String authToken;
    private static int gameID;

    @BeforeAll
    public static void init() {
        server = new Server();
        var port = server.run(0);
        System.out.println("Started test HTTP server on " + port);
        facade = new ServerFacade(port);
        try {
            authToken = facade.register("test", "test", "test");
            gameID = facade.createGame(authToken, "test");
        } catch (Exception e) {
            log.info("Database prep failed! (May already exist)");
        }
    }

    @AfterAll
    static void stopServer() {
        server.stop();
    }

    @Test
    void register() {
        String authToken;
        try {
            authToken = facade.register("test1", "test1", "test1");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        assertNotNull(authToken);
        assertEquals(36, authToken.length());
    }

    @Test
    void listGames() throws IOException {
        GameData[] games = facade.listGames(authToken);
        assertNotNull(games);
    }

    @Test
    void login() throws IOException {
        authToken = facade.login("test", "test");
        assertNotNull(authToken);
        assertEquals(36, authToken.length());
    }

    @Test
    void createGame() throws IOException {
        int gameID = facade.createGame(authToken, "test1");
        assertTrue(gameID > 99999 && gameID < 1000000);
    }

    @Test
    void joinGame() {
        assertDoesNotThrow(() -> facade.joinGame(authToken, "WHITE", gameID));
    }

    @Test
    void logout() throws IOException {
        String authToken = facade.login("test", "test");
        assertDoesNotThrow(() -> facade.logout(authToken));
    }

}
