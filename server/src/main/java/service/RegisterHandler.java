package service;

import com.google.gson.Gson;
import dataaccess.DataAccessException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import spark.Request;
import spark.Response;
import model.UserData;
import model.AuthData;

public class RegisterHandler extends HttpHandler {

    private static final Logger log = LoggerFactory.getLogger(RegisterHandler.class);

    public Object register(Request request, Response response) {
        String acceptHeader = request.headers("Accept");
        Gson gson = new Gson();
        UserData registerRequest = gson.fromJson(request.body(), UserData.class);
        UserService registerService = new UserService();
        try {
            AuthData registerResult = registerService.register(registerRequest);
            return gson.toJson(registerResult);
        } catch (DataAccessException e) {
            log.error("e: ", e);
            if (e.getMessage() != null) {
                if (e.getMessage().equals("Error: Forbidden")) {
                    response.status(403);
                } else if (e.getMessage().equals("Error: Unauthorized")) {
                    response.status(401);
                } else if (e.getMessage().equals("Error: Bad request")) {
                    response.status(400);
                }
            }
            return "{\"message\":\"error\"}";
        }


    }
}
