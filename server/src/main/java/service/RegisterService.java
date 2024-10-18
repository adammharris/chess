package service;

import handler.RegisterResult;
import model.UserData;
import model.AuthData;

public class RegisterService {
    public AuthData register(UserData registerRequest) {
        String authToken = "placeholder";
        // TODO: Get authToken from dataaccess
        return new AuthData(registerRequest.username(), authToken);
    }
}
