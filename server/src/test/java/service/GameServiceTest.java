package service;

import chess.ChessGame;
import dataaccess.DataAccessException;
import dataaccess.MemoryAuthDAO;
import model.AuthData;
import model.GameData;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import spark.Request;

import static org.junit.jupiter.api.Assertions.*;

class GameServiceTest extends ServiceTest {

    private static GameData existingGame;
    private static GameService gameService;

    @AfterAll
    static void serverStop() {
        myServer.stop();
    }

    @BeforeAll
    public static void init() {
        startServer();

        gameService = new GameService();
        existingGame = new GameData(1, "whiteUser1", "blackUser1", "game1", new ChessGame());
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
        assertNull(updated.whiteUsername());
    }

    @Test
    void clear() {
        gameService.createGame("clearMe");
        gameService.clear();
        GameData[] games = gameService.listGames();
        assertEquals(0, games.length);
    }
}