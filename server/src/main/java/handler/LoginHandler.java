package handler;

import com.google.gson.Gson;
import service.AuthService;
import spark.Request;
import spark.Response;
import service.UserService;
import model.UserData;
import model.AuthData;

public class LoginHandler {
    public Object login(Request request, Response response) {
        String acceptHeader = request.headers("Accept");
        Gson gson = new Gson();
        UserData loginRequest = gson.fromJson(request.body(), UserData.class);
        UserService loginService = new UserService();
        AuthData loginResult = loginService.login(loginRequest);
        return gson.toJson(loginResult);
    }

    public Object logout(Request request, Response response) {
        String acceptHeader = request.headers("Accept");
        AuthData logoutRequest = new AuthData(request.headers("Authorization"), "");
        AuthService logoutService = new AuthService();
        if (!logoutService.validateAuthToken(logoutRequest)) {
            response.status(401);
            return "{\"message\":\"Error: unauthorized\"}";
        }
        logoutService.deleteAuth(logoutRequest);
        return "{}";
    }
}
