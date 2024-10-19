package service;

import model.GameData;
import chess.ChessGame;

public class GameService {
    public GameData createGame(String gameName) {
        // TODO: add game to data access
        return new GameData(1, "white", "black", "name", new ChessGame());
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
        return createGame("updateGame");
    }
}
