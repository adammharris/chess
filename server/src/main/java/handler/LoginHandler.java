package handler;

import com.google.gson.Gson;
import service.AuthService;
import spark.Request;
import spark.Response;
import service.UserService;
import model.UserData;
import model.AuthData;

public class LoginHandler extends HttpHandler {
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
        String res = validateAuthToken(request, response);
        if (!res.equals("{}")) return res;
        AuthData logoutRequest = new AuthData(request.headers("Authorization"), "");
        AuthService logoutService = new AuthService();
        logoutService.deleteAuth(logoutRequest);
        return res;
    }
}