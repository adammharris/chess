package dataaccess;

import model.UserData;

public interface UserDAO {
    /**
     * Adds user to database
     * @param user UserData object representing user
     * @throws DataAccessException Throws if adding user fails
     */
    void createUser(UserData user) throws DataAccessException;

    /**
     * Retrieves user from database
     * @param user Unique string representing user
     * @return UserData object containing given username
     * @throws DataAccessException Throws if username is not found
     */
    UserData getUser(UserData user) throws DataAccessException;

    /**
     * Deletes user from database
     * @param username Unique string representing user
     * @throws DataAccessException Throws if username is not found
     */
    void deleteUser(String username) throws DataAccessException;

    /**
     * Deletes all users from database
     */
    void clear();
}
