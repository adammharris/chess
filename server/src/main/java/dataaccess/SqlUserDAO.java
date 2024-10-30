package dataaccess;

import model.UserData;
import java.sql.SQLException;

//TODO: abstract SQL access into SqlDao
public class SqlUserDAO extends SqlDAO implements UserDAO {
    private static SqlUserDAO instance;
    private SqlUserDAO() {}

    public static SqlUserDAO getInstance() {
        if (instance == null) {
            instance = new SqlUserDAO();
        }
        return instance;
    }

    @Override
    public void createUser(UserData user) throws DataAccessException {
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
