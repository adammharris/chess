package service;

import dataaccess.DataAccessException;
import dataaccess.MemoryGameDAO;
import model.GameData;
import chess.ChessGame;

import java.util.HashMap;

public class GameService {
    //HashMap<int, GameData> games = new HashMap();
    public GameData createGame(String gameName) {
        // TODO: add game to data access
        MemoryGameDAO gameDAO = MemoryGameDAO.getInstance();
        GameData newGame = new GameData(0, "", "", gameName, new ChessGame());
        try {
            gameDAO.createGame(newGame);
        } catch (DataAccessException e) {
            throw new RuntimeException("Failed to create game");
        }

        return newGame;
    }
    public GameData getGame(int gameID) {
        MemoryGameDAO gameDAO = MemoryGameDAO.getInstance();
        GameData game;
        try {
            game = gameDAO.getGame(gameID);
        } catch (DataAccessException e) {
            return null;
        }
        return game;
    }
    public GameData[] listGames() {
        MemoryGameDAO gameDAO = MemoryGameDAO.getInstance();
        HashMap<Integer, GameData> games = gameDAO.getGames();
        return games.values().toArray(new GameData[0]);
    }

    public GameData updateGame(String playerColor, int gameID) {
        // TODO: actually update game
        return createGame("updateGame");
    }

    public void clear() {
        MemoryGameDAO gameDAO = MemoryGameDAO.getInstance();
        gameDAO.clear();
    }
}
