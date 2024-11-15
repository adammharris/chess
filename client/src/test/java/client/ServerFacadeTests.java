package client;

import model.GameData;
import org.junit.jupiter.api.*;
import server.Server;

import java.io.IOException;
import static org.junit.jupiter.api.Assertions.*;


public class ServerFacadeTests {

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
    }

    @BeforeEach
    void clear() throws IOException {
        facade.clear();
        authToken = facade.register("test", "test", "test");
        gameID = facade.createGame(authToken, "test");
        //facade.logout(authToken);
    }

    @AfterAll
    static void stopServer() throws IOException {
        facade.clear();
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

    @Test void badRegister() {
        assertThrows(IOException.class, () -> facade.register(null, null, null));
    }

    @Test
    void listGames() throws IOException {
        GameData[] games = facade.listGames(authToken);
        assertNotNull(games);
    }
    @Test
    void badListGames() {
        assertThrows(IOException.class, () -> facade.listGames(""));
    }

    @Test
    void login() throws IOException {
        authToken = facade.login("test", "test");
        assertNotNull(authToken);
        assertEquals(36, authToken.length());
    }

    @Test
    void badLogin() {
        assertThrows(IOException.class, () -> facade.login("doesNotExist", "atAll"));
    }

    @Test
    void createGame() throws IOException {
        int gameID = facade.createGame(authToken, "test1");
        assertTrue(gameID > 99999 && gameID < 1000000);
    }
    @Test
    void badCreateGame() {
        assertThrows(IOException.class, () -> facade.createGame("unauthorized", "test1"));
    }

    @Test
    void joinGame() {
        assertDoesNotThrow(() -> facade.joinGame(authToken, "WHITE", gameID));
    }

    @Test
    void badJoinGame() {
        assertThrows(IOException.class, () -> facade.joinGame("unauthorized", "WHITE", gameID));
    }

    @Test
    void logout() {
        //String authToken = facade.login("test", "test");
        assertDoesNotThrow(() -> facade.logout(authToken));
    }

    @Test
    void badLogout() {
        assertThrows(IOException.class, () -> facade.logout("unauthorized"));
    }

}
