package handler;

import com.google.gson.Gson;
import spark.Request;
import spark.Response;
import service.UserService;
import model.UserData;
import model.AuthData;

public class RegisterHandler {

    public Object register(Request request, Response response) {
        String acceptHeader = request.headers("Accept");
        Gson gson = new Gson();
        UserData registerRequest = gson.fromJson(request.body(), UserData.class);
        UserService registerService = new UserService();
        AuthData registerResult = registerService.register(registerRequest);
        return gson.toJson(registerResult);
    }
}
