package service;

import dataaccess.DataAccessException;
import dataaccess.MemoryUserDAO;
import model.AuthData;
import model.UserData;

public class UserService {
    private final static MemoryUserDAO userDAO = MemoryUserDAO.getInstance();

    public AuthData register(UserData user) throws DataAccessException {
        userDAO.createUser(user);
        return login(user);
    }

    public AuthData login(UserData user) throws DataAccessException {
        // Check if user exists
        userDAO.getUser(user);

        // Create new AuthData
        AuthService as = new AuthService();
        return as.createAuth(user.username());
    }

    public void logout(AuthData auth) {
        AuthService as = new AuthService();
        as.deleteAuth(auth);
    }

    public void clear() {
        userDAO.clear();
    }
}
