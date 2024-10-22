package dataaccess;

import model.UserData;

import java.util.HashMap;

public class MemoryUserDAO implements UserDAO {
    private HashMap<String, UserData> users = new HashMap<>();
    private static MemoryUserDAO instance;

    private MemoryUserDAO() {}

    public static MemoryUserDAO getInstance() {
        if (instance == null) {
            instance = new MemoryUserDAO();
        }
        return instance;
    }
    @Override
    public void createUser(UserData user) throws DataAccessException {
        UserData previous = users.put(user.username(), user);
        if (previous != null) throw new DataAccessException("Previous data found with name " + user.username());
    }

    @Override
    public UserData getUser(UserData loginRequest) throws DataAccessException {
        UserData user = users.get(loginRequest.username());
        if (user == null) throw new DataAccessException("username not found, cannot get");
        if (!loginRequest.password().equals(user.password())) throw new DataAccessException("Incorrect password");
        return user;
    }

    @Override
    public void deleteUser(String username) throws DataAccessException {
        UserData user = users.remove(username);
        if (user == null) throw new DataAccessException(username + " not found, cannot delete");
    }

    @Override
    public void clear() {
        users.clear();
    }
}
