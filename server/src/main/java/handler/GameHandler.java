package handler;

import com.google.gson.Gson;
import service.GameService;
import spark.Request;
import spark.Response;
import model.*;

public class GameHandler extends HttpHandler {
    public Object create(Request request, Response response) {
        String acceptHeader = request.headers("Accept");

        Gson gson = new Gson();
        GameData createGameRequest = gson.fromJson(request.body(), GameData.class);
        GameService createGameService = new GameService();
        GameData createGameResult = createGameService.createGame(createGameRequest.gameName());

        return "{\"gameID\":\"%s\"}".formatted(createGameResult.gameID());
    }

    public Object list(Request request, Response response) {
        String acceptHeader = request.headers("Accept");

        String res = validateAuthToken(request, response);
        if (!res.equals("{}")) return res;

        GameService listGameService = new GameService();
        Gson gson = new Gson();
        return "{\"games\":" + gson.toJson(listGameService.listGames()) + "}";
    }

    public Object join(Request request, Response response) {
        String acceptHeader = request.headers("Accept");

        String res = validateAuthToken(request, response);
        if (!res.equals("{}")) return res;

        // TODO: actually join

        return "{}";
    }
}
