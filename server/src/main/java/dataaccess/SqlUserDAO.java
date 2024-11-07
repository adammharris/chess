package dataaccess;

import model.UserData;
import org.mindrot.jbcrypt.BCrypt;

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
            if (currentUser.username().equals(user.username())) {
                throw new DataAccessException("Error: Forbidden");
            }
        }
        String hashedPassword = BCrypt.hashpw(user.password(), BCrypt.gensalt());
        UserData userWithHashedPassword = new UserData(user.username(), hashedPassword, user.email());
        create(new String[]{"username", "password", "user"}, new String[]{user.username(), hashedPassword, gson.toJson(userWithHashedPassword)});
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
        if (!BCrypt.checkpw(loginRequest.password(), user.password())) {
            throw new DataAccessException("Error: Unauthorized");
        }
        return user;
    }
}
