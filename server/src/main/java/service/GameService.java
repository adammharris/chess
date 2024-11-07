package service;

import dataaccess.*;
import model.GameData;
import spark.Request;

public class GameService {
    private static final SqlGameDAO gameDAO = SqlGameDAO.getInstance();

    public GameData createGame(String gameName) {
        GameData newGame;
        gameName = gameName.replace("'", "\\'");
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
        return gameDAO.getGames();
    }

    public GameData updateGame(String playerColor, int gameID, Request request) throws DataAccessException {
        GameData game = getGame(gameID);
        GameData updatedGame = null;
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
            if (game.blackUsername() == null) {
                updatedGame = new GameData(gameID, username, "null", game.gameName(), game.game());
            } else {
                updatedGame = new GameData(gameID, username, game.blackUsername(), game.gameName(), game.game());
            }
        } else if (playerColor.equals("BLACK")) {
            if (game.blackUsername() != null) {
                throw new DataAccessException("Error: Forbidden");
            }
            if (game.whiteUsername() == null) {
                updatedGame = new GameData(gameID, "null", username, game.gameName(), game.game());
            } else {
                updatedGame = new GameData(gameID, game.whiteUsername(), username, game.gameName(), game.game());
            }
        }
        gameDAO.updateGame(updatedGame);
        return updatedGame;
    }

    public void clear() {
        gameDAO.clear();
    }
}
