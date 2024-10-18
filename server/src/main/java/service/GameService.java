package service;

import model.GameData;
import chess.ChessGame;

public class GameService {
    GameData createGame() {
        // TODO: add game to data access
        return new GameData(1, "white", "black", "name", new ChessGame());
    }
    GameData getGame() {
        ChessGame game = new ChessGame();
        // TODO: Get game from data access
        return createGame();
    }
    GameData[] listGames() {
        // TODO: Get games from data access
        GameData[] games = {createGame(), createGame()};
        return games;
    }

    GameData updateGame() {
        return getGame();
    }
}
