package dataaccess;

import model.AuthData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SqlAuthDAOTest {
    private final static SqlAuthDAO authDAO = SqlAuthDAO.getInstance();

    @BeforeEach
    void setUp() {
        authDAO.clear();
    }

    @Test
    void testCreateAuth() {
        final AuthData result;
        try {
            result = authDAO.createAuth("username");
        } catch (DataAccessException e) {
            throw new RuntimeException(e);
        }
        assertNotNull(result);
        assertEquals(result.username(), "username");
    }

    @Test
    void testGetAuth() {
        final AuthData auth1;
        try {
            auth1 = authDAO.createAuth("username");
        } catch (DataAccessException e) {
            throw new RuntimeException(e);
        }
        final AuthData auth2;
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
        final AuthData auth;
        try {
            auth = authDAO.createAuth("username");
        } catch (DataAccessException e) {
            throw new RuntimeException(e);
        }
        assertDoesNotThrow(() -> authDAO.deleteAuth(auth.authToken()));
        //AuthData auth2 = null;
        assertThrows(DataAccessException.class, () -> authDAO.getAuth(auth.authToken()));
        //assertNull(auth2);
    }

    @Test
    void testClear() {
        final AuthData auth1;
        final AuthData auth2;
        try {
            auth1 = authDAO.createAuth("username1");
            auth2 = authDAO.createAuth("username2");
        } catch (DataAccessException e) {
            throw new RuntimeException(e);
        }

        authDAO.clear();
        assertThrows(DataAccessException.class, () -> authDAO.getAuth(auth1.authToken()));
        assertThrows(DataAccessException.class, () -> authDAO.getAuth(auth2.authToken()));
    }
}