package service;

import model.AuthData;
import java.util.UUID;

public class AuthService {
    AuthData createAuth() {
        return new AuthData(UUID.randomUUID().toString(), "AuthUsername");
    }
    void deleteAuth() {
        // TODO:
    }
}
