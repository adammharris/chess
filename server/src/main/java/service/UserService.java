package service;

import model.AuthData;
import model.UserData;

public class UserService {
    public AuthData register(UserData user) {
        // TODO: Get authToken from data access
        return new AuthData("authToken from register", user.username());
    }
    public AuthData login(UserData user) {
        // TODO: Get authData from data access
        return new AuthData("authToken from login", user.username());
    }
    public void logout(AuthData auth) {
        // TODO: Delete authData from data access
        System.out.println(auth.toString());
    }
}
