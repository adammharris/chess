package service;

import dataaccess.DataAccessException;
import dataaccess.SqlUserDAO;
import model.AuthData;
import model.UserData;

public class UserService {
    private final static SqlUserDAO USER_DAO = SqlUserDAO.getInstance();

    public AuthData register(UserData user) throws DataAccessException {
        USER_DAO.createUser(user);
        return login(user);
    }

    public AuthData login(UserData user) throws DataAccessException {
        // Check if user exists
        USER_DAO.getUser(user);

        // Create new AuthData
        AuthService as = new AuthService();
        return as.createAuth(user.username());
    }

    public void logout(AuthData auth) {
        AuthService as = new AuthService();
        as.deleteAuth(auth);
    }

    public void clear() {
        USER_DAO.clear();
    }
}
