package dataaccess;

import chess.ChessGame;
import model.GameData;
import java.util.HashMap;

public class MemoryGameDAO implements GameDAO {
    private static MemoryGameDAO instance;
    private final HashMap<Integer, GameData> games = new HashMap<>();
    private MemoryGameDAO() {}

    public static MemoryGameDAO getInstance() {
        if (instance == null) {
            instance = new MemoryGameDAO();
        }
        return instance;
    }

    @Override
    public GameData createGame(String gameName) throws DataAccessException {
        java.util.Random rand = new java.util.Random();
        GameData gameToAdd = new GameData(rand.nextInt(100000), null, null, gameName, new ChessGame());
        if (games.get(gameToAdd.gameID()) != null) {
            throw new DataAccessException("Already exists, cannot create");
        }
        games.put(gameToAdd.gameID(), gameToAdd);
        return gameToAdd;
    }

    @Override
    public GameData getGame(int gameID) throws DataAccessException {
        GameData game = games.get(gameID);
        if (game == null) {
            throw new DataAccessException("Error: Bad request");
        }
        return game;
    }

    @Override
    public void clear() {
        games.clear();
    }

    public void updateGame(GameData game) throws DataAccessException {
        GameData updatedGame = null;
        getGame(game.gameID());
        MemoryAuthDAO authDAO = MemoryAuthDAO.getInstance();
        if (game.whiteUsername() != null) {
            if (game.whiteUsername().length() == 36) {
                updatedGame = new GameData(game.gameID(),
                        authDAO.getUsername(game.whiteUsername()),
                        game.blackUsername(),
                        game.gameName(),
                        game.game());
            } else if (game.blackUsername() != null) {
                if (game.blackUsername().length() == 36) {
                    updatedGame = new GameData(game.gameID(), game.whiteUsername(), authDAO.getUsername(game.blackUsername()), game.gameName(), game.game());
                }
            }
        }
        GameData previousGame;
        if (updatedGame != null) {
            previousGame = games.put(game.gameID(), updatedGame);
        } else {
            previousGame = games.put(game.gameID(), game);
        }

        if (previousGame == null) {
            throw new DataAccessException("Error: Bad request");
        }
    }

    public HashMap<Integer, GameData> getGames() {
        return games;
    }
}
