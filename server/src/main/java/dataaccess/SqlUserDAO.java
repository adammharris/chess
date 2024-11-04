package dataaccess;

import model.UserData;

public class SqlUserDAO extends SqlDAO implements UserDAO {
    private static SqlUserDAO instance;
    public static SqlUserDAO getInstance() {
        if (instance == null) {
            instance = new SqlUserDAO();
        }
        return instance;
    }

    private SqlUserDAO() {
        setTable("users", "(username VARCHAR(128), password VARCHAR(128), user JSON)");
    }

    @Override
    public void createUser(UserData user) throws DataAccessException {
        UserData currentUser = null;
        try {
            currentUser = get("username", user.username(), "user", UserData.class);
        } catch (DataAccessException e) {
            if (!e.getMessage().contains("Unauthorized")) {
                throw new RuntimeException(e);
            }
        }
        if (currentUser != null) {
            if (currentUser.equals(user)) {
                throw new DataAccessException("Error: Forbidden");
            }
        }
        create(new String[]{"username", "password", "user"}, new String[]{user.username(), user.password(), gson.toJson(user)});
    }

    @Override
    public UserData getUser(UserData loginRequest) throws DataAccessException {
        UserData user = get("username", loginRequest.username(), "user", UserData.class);
        if (user == null) {
            throw new DataAccessException("Error: Unauthorized");
        }
        if (loginRequest.password() == null) {
            throw new DataAccessException("Error: Bad request");
        }
        if (!loginRequest.password().equals(user.password())) {
            throw new DataAccessException("Error: Unauthorized");
        }
        return user;
    }
}
