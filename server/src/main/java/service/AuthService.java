package service;

import model.AuthData;
import java.util.UUID;

public class AuthService {
    AuthData createAuth(String username) {
        // TODO: actually create Auth
        return new AuthData(UUID.randomUUID().toString(), username);
    }
    public void deleteAuth(AuthData user) {
        // TODO: remove AuthData from data access
    }
    public boolean validateAuthToken(String authToken) {
        // TODO: find AuthToken from data access
        return true;
    }
}
