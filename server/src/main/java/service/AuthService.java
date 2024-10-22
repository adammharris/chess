package service;

import dataaccess.DataAccessException;
import dataaccess.MemoryAuthDAO;
import model.AuthData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AuthService {
    private static final Logger log = LoggerFactory.getLogger(AuthService.class);
    //private static final Logger log = LoggerFactory.getLogger(AuthService.class);

    AuthData createAuth(String username) {
        MemoryAuthDAO authDAO = MemoryAuthDAO.getInstance();
        AuthData auth;
        try {
            auth = authDAO.createAuth(username);
        } catch (DataAccessException e) {
            log.error("e: ", e);
            return new AuthData("", "Already logged in");
        }
        return auth;
    }
    public void deleteAuth(AuthData user) {
        MemoryAuthDAO authDAO = MemoryAuthDAO.getInstance();
        try {
            authDAO.deleteAuth(user.authToken());
        } catch (DataAccessException e) {

        }
    }
    public boolean validateAuthToken(String authToken) {
        MemoryAuthDAO authDAO = MemoryAuthDAO.getInstance();
        String username = "";
        try {
            authDAO.getUsername(authToken);
        } catch (DataAccessException e) {
            return false;
        }
        return true;
    }

    public void clear() {
        MemoryAuthDAO authDAO = MemoryAuthDAO.getInstance();
        authDAO.clear();
    }
}
