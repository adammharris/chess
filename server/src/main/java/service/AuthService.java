package service;

import dataaccess.DataAccessException;
import dataaccess.MemoryAuthDAO;
import model.AuthData;

public class AuthService {

    AuthData createAuth(String username) {
        MemoryAuthDAO authDAO = MemoryAuthDAO.getInstance();
        AuthData auth;
        auth = authDAO.createAuth(username);
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
