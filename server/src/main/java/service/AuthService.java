package service;

import dataaccess.DataAccessException;
import dataaccess.MemoryAuthDAO;
import dataaccess.SqlAuthDAO;
import model.AuthData;

public class AuthService {
    static SqlAuthDAO authDAO = SqlAuthDAO.getInstance();

    AuthData createAuth(String username) throws DataAccessException {
        AuthData auth;
        auth = authDAO.createAuth(username);
        return auth;
    }

    public void deleteAuth(AuthData user) {
        try {
            authDAO.deleteAuth(user.authToken());
        } catch (DataAccessException e) {
            throw new RuntimeException(e);
        }
    }

    public boolean validateAuthToken(String authToken) {
        try {
            authDAO.getUsername(authToken);
        } catch (DataAccessException e) {
            return false;
        }
        return true;
    }

    public void clear() {
        authDAO.clear();
    }
}
