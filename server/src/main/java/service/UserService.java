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

    public AuthData register(UserData user) throws DataAccessException {
        MemoryUserDAO userDAO = MemoryUserDAO.getInstance();
        userDAO.createUser(user);
        return login(user);
    }
    public AuthData login(UserData user) throws DataAccessException {
        //AuthService as = new AuthService();
        // Check if user exists
        MemoryUserDAO userDAO = MemoryUserDAO.getInstance();
        userDAO.getUser(user);

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
