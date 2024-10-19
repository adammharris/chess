package handler;

import service.AuthService;
import spark.Request;
import spark.Response;

abstract class HttpHandler {
    public String validateAuthToken(Request request, Response response) {

        String authToken = request.headers("Authorization");
        AuthService validator = new AuthService();
        if (!validator.validateAuthToken(authToken)) {
            response.status(401);
            return "{\"message\":\"Error: unauthorized\"}";
        }
        return "{}";
    }
}
