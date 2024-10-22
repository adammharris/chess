package dataaccess;

import model.GameData;
import java.util.HashMap;

public class MemoryGameDAO implements GameDAO {
    private static MemoryGameDAO instance;
    private HashMap<Integer, GameData> games = new HashMap<>();
    private MemoryGameDAO() {}

    public static MemoryGameDAO getInstance() {
        if (instance == null) {
            instance = new MemoryGameDAO();
        }
        return instance;
    }

    @Override
    public void createGame(GameData game) throws DataAccessException {
        GameData previousAuth = games.put(game.gameID(), game);
        if (previousAuth != null) throw new DataAccessException("Already exists, cannot create");
    }

    @Override
    public GameData getGame(int gameID) throws DataAccessException {
        GameData game = games.get(gameID);
        if (game == null) throw new DataAccessException(gameID + "not found, cannot get");
        return game;
    }

    @Override
    public void deleteGame(int gameID) throws DataAccessException {
        GameData auth = games.remove(gameID);
        if (auth == null) throw new DataAccessException(gameID + " not found, cannot delete");
    }

    @Override
    public void clear() {
        games.clear();
    }

    public void updateGame(GameData game) throws DataAccessException {
        GameData previousGame = games.put(game.gameID(), game);
        if (previousGame == null) throw new DataAccessException(game.gameID() + " not found, cannot update");
    }
}
