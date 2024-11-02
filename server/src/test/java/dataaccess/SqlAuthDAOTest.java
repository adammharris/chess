package dataaccess;

import model.AuthData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SqlAuthDAOTest {
    SqlAuthDAO authDAO = SqlAuthDAO.getInstance();
    @BeforeEach
    void setUp() {
        authDAO.clear();
    }

    @Test
    void testCreateAuth() {
        AuthData result = authDAO.createAuth("username");
        assertNotNull(result);
        assertEquals(result.username(), "username");
    }

    @Test
    void testGetAuth() {
        AuthData auth1 = authDAO.createAuth("username");
        AuthData auth2 = null;
        try {
            auth2 = authDAO.getAuth(auth1.authToken());
        } catch (DataAccessException e) {
            throw new RuntimeException(e);
        }
        assertNotNull(auth2);
        assertEquals(auth1, auth2);
    }

    @Test
    void testDeleteAuth() {
        AuthData auth = authDAO.createAuth("username");
        assertDoesNotThrow(() -> authDAO.deleteAuth(auth.authToken()));
        AuthData auth2 = null;
        try {
            auth2 = authDAO.getAuth(auth.authToken());
        } catch (DataAccessException e) {
            throw new RuntimeException(e);
        }
        assertNull(auth2);
    }

    @Test
    void testClear() {
        //TODO: actually test this
        assertTrue(false);
    }
}