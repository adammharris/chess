package service;

import com.google.gson.Gson;
import dataaccess.DataAccessException;
import spark.Request;
import spark.Response;
import model.*;

public class GameHandler extends HttpHandler {
    private record JoinRequest(String playerColor, int gameID) {}
    public Object create(Request request, Response response) {
        String acceptHeader = request.headers("Accept");

        String res = validateAuthToken(request, response);
        if (!res.equals("{}")) {
            return res;
        }

        Gson gson = new Gson();
        GameData createGameRequest = gson.fromJson(request.body(), GameData.class);
        GameService createGameService = new GameService();
        GameData createGameResult = createGameService.createGame(createGameRequest.gameName());

        return "{\"gameID\":\"%s\"}".formatted(createGameResult.gameID());
    }

    public Object list(Request request, Response response) {
        String acceptHeader = request.headers("Accept");

        String res = validateAuthToken(request, response);
        if (!res.equals("{}")) {
            return res;
        }

        GameService listGameService = new GameService();
        Gson gson = new Gson();
        return "{\"games\":" + gson.toJson(listGameService.listGames()) + "}";
    }

    public Object join(Request request, Response response) {
        String acceptHeader = request.headers("Accept");

        String res = validateAuthToken(request, response);
        if (!res.equals("{}")) {
            return res;
        }

        GameService gs = new GameService();

        Gson gson = new Gson();
        JoinRequest jr = gson.fromJson(request.body(), JoinRequest.class);
        try {
            gs.updateGame(jr.playerColor, jr.gameID, request);
        } catch (DataAccessException e) {
            if (e.getMessage() != null) {
                if (e.getMessage().equals("Error: Bad request")) {
                    response.status(400);
                } else if (e.getMessage().equals("Error: Unauthorized")) {
                    response.status(401);
                } else if (e.getMessage().equals("Error: Forbidden")) {
                    response.status(403);
                }
                return "{\"message\":\"" + e.getMessage() + "\"}";
            }
            return "{\"message\":\"Error\"}";
        }

        return "{}";
    }
}
