package dataaccess;

import com.google.gson.Gson;
import model.AuthData;

import java.sql.SQLException;
import java.util.UUID;

public class SqlAuthDAO implements AuthDAO {
    private static SqlAuthDAO instance;
    private SqlAuthDAO() {}
    private static Gson gson = new Gson();

    static {
        try {
            DatabaseManager.createDatabase();
        } catch (Exception e) {
            throw new RuntimeException("Database setup failed:" + e);
        }
    }

    public static SqlAuthDAO getInstance() {
        if (instance == null) {
            instance = new SqlAuthDAO();
        }
        return instance;
    }

    @Override
    public AuthData createAuth(String username) throws DataAccessException {
        AuthData newAuth = new AuthData(UUID.randomUUID().toString(), username);
        try (var conn = DatabaseManager.getConnection()) {
            var json = gson.toJson(newAuth);
            var statement = "INSERT INTO chess (authToken, username, json) VALUES (%s, %s, %s)".formatted(newAuth.authToken(), username, json);
            try (var preparedStatement = conn.prepareStatement(statement)) {
                preparedStatement.executeQuery();
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        //TODO: auths.put(newAuth.authToken(), newAuth);
        return newAuth;
    }

    @Override
    public AuthData getAuth(String authToken) throws DataAccessException {
        if (authToken == null) {
            throw new DataAccessException("Error: Not found");
        }
        AuthData auth = null;
        try (var conn = DatabaseManager.getConnection()) {
            var statement = "SELECT authToken, json FROM chess WHERE authToken=%s".formatted(authToken);
            try (var preparedStatement = conn.prepareStatement(statement)) {
                try (var result = preparedStatement.executeQuery()) {
                    if (result.next()) {
                        auth = gson.fromJson(result.getString("json"), AuthData.class);
                    }
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        if (auth == null) {
            throw new DataAccessException("Error: Not found");
        }
        return auth;
    }

    @Override
    public void deleteAuth(String authToken) throws DataAccessException {
        try (var conn = DatabaseManager.getConnection()) {
            var statement = "DELETE FROM chess WHERE id=%s".formatted(authToken);
            try (var preparedStatement = conn.prepareStatement(statement)) {
                preparedStatement.executeQuery();
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void clear() {
        try (var conn = DatabaseManager.getConnection()) {
            var statement = "TRUNCATE chess";
            try (var preparedStatement = conn.prepareStatement(statement)) {
                preparedStatement.executeQuery();
            }
        } catch (SQLException | DataAccessException e) {
            throw new RuntimeException(e);
        }
        //TODO: auths.clear();
    }

    public String getUsername(String authToken) throws DataAccessException {
        AuthData auth = getAuth(authToken);
        if (auth != null) {
            return auth.username();
        }
        throw new DataAccessException("Error: Unauthorized");
    }
}
