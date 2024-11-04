package dataaccess;

import model.AuthData;

public interface AuthDAO {
    /**
     * Adds auth to database
     * @param username Unique string representing user
     * @throws DataAccessException Throws if addition fails
     */
    AuthData createAuth(String username) throws DataAccessException;

    /**
     * Retrieves auth from database given authToken
     * @param username Unique string representing user
     * @return AuthData object representing authentication information
     * @throws DataAccessException Throws if authToken is not found
     */
    AuthData getAuth(String username) throws DataAccessException;

    /**
     * Removes auth from database given authToken
     * @param authToken identifier for auth
     * @throws DataAccessException Throws if deletion fails
     */
    void deleteAuth(String authToken) throws DataAccessException;

    /**
     * Deletes all auths from database
     */
    void clear();
}
