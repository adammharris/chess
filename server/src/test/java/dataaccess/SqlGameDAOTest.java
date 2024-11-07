package dataaccess;

import model.GameData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SqlGameDAOTest {
    private final static SqlGameDAO GAME_DAO = SqlGameDAO.getInstance();
    @BeforeEach
    void setup() {
        GAME_DAO.clear();
    }
    @Test
    void createGame() {
        assertDoesNotThrow(() -> GAME_DAO.createGame("gameName"));
    }

    @Test
    void createGameBad() {
        assertThrows(DataAccessException.class, () -> GAME_DAO.createGame(null));
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
    void getGameBad() {
        assertThrows(DataAccessException.class, () -> GAME_DAO.getGame(0));
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

    @Test
    void updateGameBad() {
        assertThrows(DataAccessException.class, () -> GAME_DAO.updateGame(new GameData(0, null, null, null, null)));
    }
}