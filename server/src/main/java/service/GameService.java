package service;

import dataaccess.DataAccessException;
import dataaccess.MemoryGameDAO;
import model.GameData;
import spark.Request;

import java.util.HashMap;

public class GameService {
    private static MemoryGameDAO gameDAO = MemoryGameDAO.getInstance();

    public GameData createGame(String gameName) {
        GameData newGame;
        try {
            newGame = gameDAO.createGame(gameName);
        } catch (DataAccessException e) {
            throw new RuntimeException("Failed to create game");
        }

        return newGame;
    }
    public GameData getGame(int gameID) {
        GameData game;
        try {
            game = gameDAO.getGame(gameID);
        } catch (DataAccessException e) {
            return null;
        }
        return game;
    }
    public GameData[] listGames() {
        HashMap<Integer, GameData> games = gameDAO.getGames();
        return games.values().toArray(new GameData[0]);
    }

    public GameData updateGame(String playerColor, int gameID, Request request) throws DataAccessException {
        GameData game = getGame(gameID);
        GameData updatedGame;
        String authToken = request.headers("Authorization");
        String username = AuthService.authDAO.getUsername(authToken);
        if (playerColor == null) {
            throw new DataAccessException("Error: Bad request");
        }
        if (game == null) {
            throw new DataAccessException("Error: Bad request");
        }
        if (playerColor.equals("WHITE")) {
            if (game.whiteUsername() != null) {
                throw new DataAccessException("Error: Forbidden");
            }
            updatedGame = new GameData(gameID, username, game.blackUsername(), game.gameName(), game.game());
            gameDAO.updateGame(updatedGame);
        } else if (playerColor.equals("BLACK")) {
            if (game.blackUsername() != null) {
                throw new DataAccessException("Error: Forbidden");
            }
            updatedGame = new GameData(gameID, game.whiteUsername(), username, game.gameName(), game.game());
            gameDAO.updateGame(updatedGame);
        }
        return game;
    }

    public void clear() {
        gameDAO.clear();
    }
}
