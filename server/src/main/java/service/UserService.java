package service;

import dataaccess.DataAccessException;
import dataaccess.MemoryAuthDAO;
import dataaccess.MemoryUserDAO;
import model.AuthData;
import model.UserData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UserService {
    private static final Logger log = LoggerFactory.getLogger(UserService.class);

    public AuthData register(UserData user) {
        MemoryUserDAO userDAO = MemoryUserDAO.getInstance();
        try {
            userDAO.createUser(user);
        } catch (DataAccessException e) {
            return new AuthData("", "NAME_TAKEN");
        }
        return login(user);
    }
    public AuthData login(UserData user) {
        //AuthService as = new AuthService();
        // Check if user exists
        MemoryUserDAO userDAO = MemoryUserDAO.getInstance();
        try {
            userDAO.getUser(user);
        } catch (DataAccessException e) {
            return new AuthData("", "404");
        }

        // Check if AuthData exists
        MemoryAuthDAO authDAO = MemoryAuthDAO.getInstance();

        // Create new AuthData
        AuthService as = new AuthService();
        return as.createAuth(user.username());
    }
    public void logout(AuthData auth) {
        AuthService as = new AuthService();
        as.deleteAuth(auth);
    }

    public void clear() {
        MemoryUserDAO userDAO = MemoryUserDAO.getInstance();
        userDAO.clear();
    }
}
