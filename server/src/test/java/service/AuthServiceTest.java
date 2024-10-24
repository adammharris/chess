package service;

import dataaccess.DataAccessException;
import model.AuthData;
import model.UserData;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AuthServiceTest extends ServiceTest {

    private static UserData existingUser;
    private static UserData newUser;
    private static AuthService authService;

    @AfterAll
    static void stop() {
        myServer.stop();
    }

    @BeforeAll
    public static void init() {
        startServer();

        authService = new AuthService();
        existingUser = new UserData("ExistingUser", "existingUserPassword", "eu@mail.com");
        newUser = new UserData("NewUser", "newUserPassword", "nu@mail.com");
    }

    @Test
    void createAuth() throws DataAccessException {
        AuthData auth = authService.createAuth(existingUser.username());
        assertNotNull(auth);
        assertEquals(existingUser.username(), auth.username());

    }

    @Test
    void deleteAuth() throws DataAccessException {
        AuthData auth = authService.createAuth(newUser.username());
        assertNotNull(auth);
    }

    @Test
    void validateAuthToken() throws DataAccessException {
        AuthData auth = authService.createAuth(existingUser.username());
        assertTrue(authService.validateAuthToken(auth.authToken()));
        authService.deleteAuth(auth);
        assertFalse(authService.validateAuthToken(auth.authToken()));
    }

    @Test
    void clear() {
        authService.clear();
        assertFalse(authService.validateAuthToken("anyToken"));
    }
}