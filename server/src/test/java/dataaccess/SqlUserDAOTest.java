package dataaccess;

import model.UserData;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SqlUserDAOTest {
    private final static SqlUserDAO userDAO = SqlUserDAO.getInstance();
    @Test
    void createUser() {
        assertDoesNotThrow(() -> userDAO.createUser(new UserData("a", "b", "c")));
    }

    @Test
    void getUser() {
        final UserData result;
        try {
            userDAO.createUser(new UserData("a", "b", "c"));
            result = userDAO.getUser(new UserData("a", "b","c"));
        } catch (DataAccessException e) {
            throw new RuntimeException(e);
        }
        assertNotNull(result);
    }
}