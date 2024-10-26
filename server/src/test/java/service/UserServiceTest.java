package service;

import dataaccess.DataAccessException;
import model.AuthData;
import model.UserData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
class UserServiceTest {
    private UserService us;
    private UserData ud;
    private UserData badUser;

    @BeforeEach
    void setup() {
        us = new UserService();
        ud = new UserData("username", "password", "email");
        badUser = new UserData(null, null, null);
        us.clear();
    }

    @Test
    void register() throws DataAccessException {
        AuthData result = us.register(ud);
        assertNotNull(result);
        assertEquals(result.username(), "username");
    }

    @Test
    void registerBadData() {
        assertThrows(DataAccessException.class, () -> us.register(badUser));
    }

    @Test
    void login() throws DataAccessException {
        AuthData auth = us.register(ud);
        us.logout(auth);
        AuthData result = us.login(ud);
        assertNotNull(result);
        assertEquals(result.username(),"username");
    }

    @Test
    void loginBadData() throws DataAccessException {
        AuthData auth = us.register(ud);
        us.logout(auth);
        assertThrows(DataAccessException.class, () -> us.login(badUser));
    }

    @Test
    void logout() throws DataAccessException {
        AuthData auth = us.register(ud);
        us.logout(auth);
        AuthData result = us.login(ud);
        assertNotNull(result);
    }

    @Test
    void logoutBadData() throws DataAccessException {
        AuthData auth = us.register(ud);
        us.logout(new AuthData("janky", "data"));
        assertDoesNotThrow(() -> us.logout(auth));
    }

    @Test
    void clear() throws DataAccessException {
        us.register(ud);
        us.clear();
        assertThrows(DataAccessException.class, () -> us.login(ud));
    }

    @Test
    void clearBadData() throws DataAccessException {
        us.register(ud);
        us.clear();
        assertThrows(DataAccessException.class, () -> us.login(ud));
    }
}