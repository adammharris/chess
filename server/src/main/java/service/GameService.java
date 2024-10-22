package service;

import dataaccess.MemoryGameDAO;
import model.GameData;
import chess.ChessGame;

import java.util.HashMap;

public class GameService {
    //HashMap<int, GameData> games = new HashMap();
    public GameData createGame(String gameName) {
        // TODO: add game to data access
        GameData newGame = new GameData(1, "white", "black", "name", new ChessGame());
        //games.put(newGame.gameID(), newGame);
        return newGame;
    }
    public GameData getGame() {
        ChessGame game = new ChessGame();
        // TODO: Get game from data access
        return createGame("getGame");
    }
    public GameData[] listGames() {
        // TODO: Get games from data access
        GameData[] games = {createGame("list"), createGame("Games")};
        return games;
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
