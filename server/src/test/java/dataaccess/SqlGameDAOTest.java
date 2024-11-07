package dataaccess;

import model.GameData;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SqlGameDAOTest {
    private final static SqlGameDAO gameDAO = SqlGameDAO.getInstance();
    @BeforeAll
    static void setup() {
        gameDAO.clear();
    }
    @Test
    void createGame() {
        assertDoesNotThrow(() -> gameDAO.createGame("gameName"));
    }

    @Test
    void getGame() {
        final GameData result;
        try {
            result = gameDAO.createGame("gameName");
        } catch (DataAccessException e) {
            throw new RuntimeException(e);
        }
        assertDoesNotThrow(() -> gameDAO.getGame(result.gameID()));
    }

    @Test
    void updateGame() {
        final GameData result;
        try {
            result = gameDAO.createGame("gameName");
        } catch (DataAccessException e) {
            throw new RuntimeException(e);
        }
        assertDoesNotThrow(() -> gameDAO.updateGame(new GameData(result.gameID(), "new", "name", result.gameName(), result.game())));

    }
}