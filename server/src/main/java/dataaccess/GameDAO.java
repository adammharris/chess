package dataaccess;

import model.GameData;

public interface GameDAO {
    /**
     * Adds game to database
     * @param game GameData object containing all information about the game
     * @throws DataAccessException Throws if information is invalid
     */
    void createGame(GameData game) throws DataAccessException;

    /**
     * Retrieves game from database given gameID
     * @param gameID Unique identifier for game
     * @return GameData object representing game
     * @throws DataAccessException Throws if no game with gameID is found
     */
    GameData getGame(int gameID) throws DataAccessException;

    /**
     * Removes game from database given gameID
     * @param gameID Unique identifier for game
     * @throws DataAccessException Throws if no game with gameID is found
     */
    void deleteGame(int gameID) throws DataAccessException;

    /**
     * Updates game in database.
     * @param game GameData object representing updated game
     * @throws DataAccessException Throws if no game with gameID is found
     */
    void updateGame(GameData game) throws DataAccessException;

    /**
     * Deletes all games from the database
     */
    void clear();
}
