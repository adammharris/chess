package dataaccess;

import com.google.gson.Gson;
import model.UserData;
import java.sql.SQLException;

public class SqlUserDAO extends SqlDAO implements UserDAO {
    private static SqlUserDAO instance;
    private SqlUserDAO() {}
    private static Gson gson = new Gson();

    static {
        try {
            DatabaseManager.createDatabase();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    public static SqlUserDAO getInstance() {
        if (instance == null) {
            instance = new SqlUserDAO();
        }
        return instance;
    }

    @Override
    public void createUser(UserData user) throws DataAccessException {
        UserData currentUser = getUser(user);
        if (currentUser != null) {
            if (currentUser.equals(user)) {
                throw new DataAccessException("Error: Forbidden");
            }
        }
        //TODO: users.put(user.username(), user);
        try (var conn = DatabaseManager.getConnection()) {
            var json = gson.toJson(user);
            var statement = "INSERT INTO chess (username, password, email, user) VALUES ";
            statement = statement.concat("(%s, %s, %s, %s").formatted(user.username(), user.password(), user.email(), json);
            try (var preparedStatement = conn.prepareStatement(statement)) {
                preparedStatement.executeQuery();
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public UserData getUser(UserData loginRequest) throws DataAccessException {
        if (loginRequest.password() == null) {
            throw new DataAccessException("Error: Bad request");
        }
        UserData user = null;
        try (var conn = DatabaseManager.getConnection()) {
            var statement = "SELECT username, user FROM chess WHERE password=%s".formatted(loginRequest.password());
            try (var preparedStatement = conn.prepareStatement(statement)) {
                try (var result = preparedStatement.executeQuery()) {
                    if (result.next()) {
                        user = gson.fromJson(result.getString("user"), UserData.class);
                    }
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        if (user == null) {
            throw new DataAccessException("Error: Unauthorized");
        }
        if (!loginRequest.password().equals(user.password())) {
            throw new DataAccessException("Error: Unauthorized");
        }

        return user;
    }
}
