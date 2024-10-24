package service;

import dataaccess.DataAccessException;
import dataaccess.MemoryUserDAO;
import model.AuthData;
import model.UserData;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class UserServiceTest extends ServiceTest {

    private static UserService userService;
    private static UserData existingUser;
    private static UserData newUser;

    @BeforeAll
    public static void init() {
        startServer();

        userService = new UserService();
        existingUser = new UserData("ExistingUser", "existingUserPassword", "eu@mail.com");
        newUser = new UserData("NewUser", "newUserPassword", "nu@mail.com");
    }

    @AfterAll
    static void stopServer() {
        myServer.stop();
    }

    @Test
    void register() throws DataAccessException {
        AuthData auth = userService.register(newUser);
        assertNotNull(auth);
        assertEquals(newUser.username(), auth.username());
        userService.logout(auth);
    }

    @Test
    void login() throws DataAccessException {
        userService.register(existingUser);
        AuthData auth = userService.login(existingUser);
        assertNotNull(auth);
        assertEquals(existingUser.username(), auth.username());
    }

    @Test
    void logout() throws DataAccessException{
        AuthData auth = userService.register(existingUser);
        userService.logout(auth);
        AuthService authService = new AuthService();
        assertFalse(authService.validateAuthToken(auth.authToken()));
    }

    @Test
    void clear() throws DataAccessException {
        UserData test = new UserData("lol", "pass", "mail");
        userService.register(test);
        userService.clear();
        MemoryUserDAO userDAO = MemoryUserDAO.getInstance();
        assertThrows(DataAccessException.class, () -> userDAO.getUser(test));
    }
}