package service;

import spark.Request;
import spark.Response;

public class ClearHandler {
    public Object clear(Request request, Response response) {
        request.headers("Accept");
        AuthService as = new AuthService();
        as.clear();
        UserService us = new UserService();
        us.clear();
        GameService gs = new GameService();
        gs.clear();
        return "{}";
    }
}