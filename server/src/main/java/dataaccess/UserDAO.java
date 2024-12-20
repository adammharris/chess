package dataaccess;

import model.UserData;

public interface UserDAO {
    /**
     * Adds user to database
     * @param user UserData object representing user
     * @throws DataAccessException throws when user is already made
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
     * Deletes all users from database
     */
    void clear();
}
