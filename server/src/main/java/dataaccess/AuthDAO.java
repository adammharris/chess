package dataaccess;

import model.AuthData;
import model.GameData;

public interface AuthDAO {
    void createAuth(AuthData auth) throws DataAccessException;
    AuthData getAuth(String authToken) throws DataAccessException;
    void deleteAuth(String authToken) throws DataAccessException;
    //void updateGame(GameData game) throws DataAccessException;
    void clear() throws DataAccessException;
}
