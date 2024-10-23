package service;

import chess.ChessGame;
import dataaccess.DataAccessException;
import dataaccess.MemoryAuthDAO;
import dataaccess.MemoryUserDAO;
import model.AuthData;
import model.GameData;
import model.UserData;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import server.Server;
import spark.Request;

import static org.junit.jupiter.api.Assertions.*;

class GameServiceTest {
    private static Server server;

    private String existingAuth;
    private static GameData existingGame;
    private static GameData newGame;
    private static GameService gameService;

    @AfterAll
    static void stopServer() {
        server.stop();
    }

    @BeforeAll
    public static void init() {
        server = new Server();
        var port = server.run(8080);
        System.out.println("Started test HTTP server on " + port);

        gameService = new GameService();
        existingGame = new GameData(1, "whiteUser1", "blackUser1", "game1", new ChessGame());
        newGame = new GameData(2, "whiteUser2", "blackUser2", "game2", new ChessGame());
    }

    @Test
    void createGame() {
        GameData newGame = gameService.createGame(existingGame.gameName());
        assertNotNull(newGame);
        assertEquals(existingGame.gameName(), newGame.gameName());
    }

    @Test
    void getGame() {
        GameData newGame = gameService.createGame(existingGame.gameName());
        GameData gotGame = gameService.getGame(newGame.gameID());
        assertNotNull(gotGame);
        assertEquals(newGame.gameName(), gotGame.gameName());
    }

    @Test
    void listGames() {
        gameService.createGame("woohoo");
        gameService.createGame("yay");
        GameData[] games = gameService.listGames();
        assertNotNull(games);
        assertTrue(games.length >= 2);
        //assertEquals();
    }

    @Test
    void updateGame() throws DataAccessException {
        GameData create = gameService.createGame("updateMe");

        MemoryAuthDAO authDAO = MemoryAuthDAO.getInstance();
        MemoryUserDAO userDAO = MemoryUserDAO.getInstance();
        //String thisUser = userDAO.createUser("user");
        AuthData validToken = authDAO.createAuth("user");
        Request request = new Request() {
            @Override
            public String headers(String header) {
                if ("Authorization".equals(header)) {
                    return validToken.authToken();
                }
                return null;
            }
            // Implement other methods as needed
        };

        GameData updated = gameService.updateGame("WHITE", create.gameID(), request);
        assertNotNull(updated);
        assertEquals(null, updated.whiteUsername());
    }

    @Test
    void clear() {
        gameService.createGame("clearMe");
        gameService.clear();
        GameData[] games = gameService.listGames();
        assertEquals(0, games.length);
    }
}