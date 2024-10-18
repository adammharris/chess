package handler;

import com.google.gson.Gson;
import spark.Request;
import spark.Response;
import service.RegisterService;

public class RegisterHandler {

    public Object simpleResponse(Request request, Response response) {
        String acceptHeader = request.headers("Accept");
        Gson gson = new Gson();
        RegisterRequest registerRequest = gson.fromJson(request.body(), RegisterRequest.class);
        RegisterService registerService = new RegisterService();
        RegisterResult registerResult = registerService.register(registerRequest);
        return gson.toJson(registerResult);
    }
}
