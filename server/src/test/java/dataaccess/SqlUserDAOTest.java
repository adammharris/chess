package dataaccess;

import model.UserData;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SqlUserDAOTest {
    private final static SqlUserDAO USER_DAO = SqlUserDAO.getInstance();
    @Test
    void createUser() {
        assertDoesNotThrow(() -> USER_DAO.createUser(new UserData("a", "b", "c")));
    }

    @Test
    void createUserBad() {
        assertThrows(DataAccessException.class, () -> USER_DAO.createUser(new UserData(null, null, null)));
    }

    @Test
    void getUser() {
        final UserData result;
        try {
            USER_DAO.createUser(new UserData("a", "b", "c"));
            result = USER_DAO.getUser(new UserData("a", "b","c"));
        } catch (DataAccessException e) {
            throw new RuntimeException(e);
        }
        assertNotNull(result);
    }

    @Test
    void getUserBad() {
        assertThrows(DataAccessException.class, () -> USER_DAO.getUser(new UserData(null, null, null)));
    }
}