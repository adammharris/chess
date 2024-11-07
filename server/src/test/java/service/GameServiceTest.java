package service;

import model.GameData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import spark.Request;

import static org.junit.jupiter.api.Assertions.*;

class GameServiceTest {
    private GameService gs;

    @BeforeEach
    void setup() {
        gs = new GameService();

    }
    @Test
    void createGame() {
        GameData result = gs.createGame("NewGame");
        assertNotNull(result);
    }

    @Test
    void getGame() {
        GameData game = gs.createGame("NewGame");
        GameData result = gs.getGame(game.gameID());
        assertNotNull(result);
    }

    @Test
    void listGames() {
        GameData game1 = gs.createGame("game1");
        GameData game2 = gs.createGame("game2");
        GameData[] games = gs.listGames();
        assertTrue(games[0].gameID() == game1.gameID() || games[0].gameID() == game2.gameID());
        assertTrue(games[1].gameID() == game1.gameID() || games[1].gameID() == game2.gameID());
    }

    @Test
    void updateGame() {
        GameData game1 = gs.createGame("game1");
        //GameData game2 = gs.createGame("game2");
        class Req extends Request { }
        Req req = new Req();
        assertThrows(NullPointerException.class, () -> gs.updateGame("WHITE", game1.gameID(), req));
    }

    @Test
    void clear() {
        assertDoesNotThrow(() -> gs.clear());
    }
}