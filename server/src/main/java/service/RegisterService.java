package service;

import handler.RegisterRequest;
import handler.RegisterResult;

public class RegisterService {
    public RegisterResult register(RegisterRequest registerRequest) {
        String username = registerRequest.getUsername();
        String authToken = "placeholder";
        // TODO: Get authToken from dataaccess
        return new RegisterResult(username, authToken);
    }
}
