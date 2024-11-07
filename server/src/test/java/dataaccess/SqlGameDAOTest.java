package dataaccess;

import model.GameData;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SqlGameDAOTest {
    private final static SqlGameDAO GAME_DAO = SqlGameDAO.getInstance();
    @BeforeAll
    static void setup() {
        GAME_DAO.clear();
    }
    @Test
    void createGame() {
        assertDoesNotThrow(() -> GAME_DAO.createGame("gameName"));
    }

    @Test
    void getGame() {
        final GameData result;
        try {
            result = GAME_DAO.createGame("gameName");
        } catch (DataAccessException e) {
            throw new RuntimeException(e);
        }
        assertDoesNotThrow(() -> GAME_DAO.getGame(result.gameID()));
    }

    @Test
    void updateGame() {
        final GameData result;
        try {
            result = GAME_DAO.createGame("gameName");
        } catch (DataAccessException e) {
            throw new RuntimeException(e);
        }
        assertDoesNotThrow(() -> GAME_DAO.updateGame(new GameData(result.gameID(), "new", "name", result.gameName(), result.game())));

    }
}