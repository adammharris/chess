package dataaccess;

import model.AuthData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SqlAuthDAOTest {
    private final static SqlAuthDAO AUTH_DAO = SqlAuthDAO.getInstance();

    @BeforeEach
    void setUp() {
        AUTH_DAO.clear();
    }

    @Test
    void testCreateAuth() {
        final AuthData result;
        try {
            result = AUTH_DAO.createAuth("username");
        } catch (DataAccessException e) {
            throw new RuntimeException(e);
        }
        assertNotNull(result);
        assertEquals(result.username(), "username");
    }

    @Test
    void testCreateBadAuth() {
        assertThrows(DataAccessException.class, () -> AUTH_DAO.createAuth(null));
    }

    @Test
    void testGetAuth() {
        final AuthData auth1;
        try {
            auth1 = AUTH_DAO.createAuth("username");
        } catch (DataAccessException e) {
            throw new RuntimeException(e);
        }
        final AuthData auth2;
        try {
            auth2 = AUTH_DAO.getAuth(auth1.authToken());
        } catch (DataAccessException e) {
            throw new RuntimeException(e);
        }
        assertNotNull(auth2);
        assertEquals(auth1, auth2);
    }

    @Test
    void testWrongGet() {
        assertThrows(DataAccessException.class, () -> AUTH_DAO.getAuth("test"));
    }

    @Test
    void testDeleteAuth() {
        final AuthData auth;
        try {
            auth = AUTH_DAO.createAuth("username");
        } catch (DataAccessException e) {
            throw new RuntimeException(e);
        }
        assertDoesNotThrow(() -> AUTH_DAO.deleteAuth(auth.authToken()));
        //AuthData auth2 = null;
        assertThrows(DataAccessException.class, () -> AUTH_DAO.getAuth(auth.authToken()));
        //assertNull(auth2);
    }

    @Test
    void testFalseDelete() {
        assertThrows(DataAccessException.class, () -> AUTH_DAO.deleteAuth("fake"));
    }

    @Test
    void testClear() {
        final AuthData auth1;
        final AuthData auth2;
        try {
            auth1 = AUTH_DAO.createAuth("username1");
            auth2 = AUTH_DAO.createAuth("username2");
        } catch (DataAccessException e) {
            throw new RuntimeException(e);
        }

        AUTH_DAO.clear();
        assertThrows(DataAccessException.class, () -> AUTH_DAO.getAuth(auth1.authToken()));
        assertThrows(DataAccessException.class, () -> AUTH_DAO.getAuth(auth2.authToken()));
    }
    @Test
    void testClearEmpty() {
        assertDoesNotThrow(AUTH_DAO::clear);
    }
}