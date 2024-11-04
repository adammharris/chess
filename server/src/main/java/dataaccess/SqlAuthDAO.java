package dataaccess;

import model.AuthData;

import java.sql.SQLException;
import java.util.UUID;

public class SqlAuthDAO extends SqlDAO implements AuthDAO {
    private static SqlAuthDAO instance;
    private SqlAuthDAO() {
        setupTable();
    }

    private static void setupTable() {
        tableName = "auths";
        try (var conn = DatabaseManager.getConnection()) {
            var statement = "CREATE TABLE IF NOT EXISTS auths (authToken VARCHAR(36), username VARCHAR(128), auth JSON)";
            try (var preparedStatement = conn.prepareStatement(statement)) {
                preparedStatement.executeUpdate();
            }
        } catch (SQLException | DataAccessException e) {
            throw new RuntimeException(e);
        }
    }

    public static SqlAuthDAO getInstance() {
        if (instance == null) {
            instance = new SqlAuthDAO();
        }
        return instance;
    }

    @Override
    public AuthData createAuth(String username) {
        AuthData newAuth = new AuthData(UUID.randomUUID().toString(), username);
        var auth = gson.toJson(newAuth);
        create(new String[]{"authToken", "username", "auth"}, new String[]{newAuth.authToken(), username, auth});
        return newAuth;
    }

    @Override
    public AuthData getAuth(String authToken) throws DataAccessException {
        return get("authToken", authToken, "auth", AuthData.class);
    }

    @Override
    public void deleteAuth(String authToken) throws DataAccessException {
        delete("authToken", authToken);
    }

    public String getUsername(String authToken) throws DataAccessException {
        AuthData auth = getAuth(authToken);
        if (auth != null) {
            return auth.username();
        }
        throw new DataAccessException("Error: Unauthorized");
    }
}
