package dataaccess;

import model.UserData;

import java.util.HashMap;

public class MemoryUserDAO implements UserDAO {
    private final HashMap<String, UserData> users = new HashMap<>();
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
        UserData currentUser = users.get(user.username());
        if (currentUser != null) {
            if (currentUser.equals(user)) {
                throw new DataAccessException("Error: Forbidden");
            }
        }
        //if (users.get(user.username()) != null) throw new DataAccessException("Error: Forbidden");
        users.put(user.username(), user);

    }

    @Override
    public UserData getUser(UserData loginRequest) throws DataAccessException {
        UserData user = users.get(loginRequest.username());
        if (user == null) {
            throw new DataAccessException("Error: Unauthorized");
        }
        if (loginRequest.password() == null) {
            throw new DataAccessException("Error: Bad request");
        }
        if (user != null) {
            if (!loginRequest.password().equals(user.password())) {
                throw new DataAccessException("Error: Unauthorized");
            }
        }

        return user;
    }

    @Override
    public void clear() {
        users.clear();
    }
}
