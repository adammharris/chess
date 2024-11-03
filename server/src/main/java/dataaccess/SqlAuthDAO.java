package dataaccess;

import model.AuthData;

import java.sql.SQLException;
import java.util.UUID;

//TODO: abstract SQL access into SqlDAO
public class SqlAuthDAO extends SqlDAO implements AuthDAO {
    private static SqlAuthDAO instance;
    private SqlAuthDAO() {
        setupTable();
    }

    private static void setupTable() {
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
        try (var conn = DatabaseManager.getConnection()) {
            var auth = gson.toJson(newAuth);
            var statement = "INSERT INTO auths (authToken, username, auth) VALUES ('%s', '%s', '%s')".formatted(newAuth.authToken(), username, auth);
            try (var preparedStatement = conn.prepareStatement(statement)) {
                preparedStatement.executeUpdate();
            }
        } catch (SQLException | DataAccessException e) {
            throw new RuntimeException(e);
        }
        return newAuth;
    }

    @Override
    public AuthData getAuth(String authToken) throws DataAccessException {
        if (authToken == null) {
            throw new DataAccessException("Error: Not found");
        }
        AuthData auth = null;
        try (var conn = DatabaseManager.getConnection()) {
            var statement = "SELECT authToken, auth FROM auths WHERE authToken='%s'".formatted(authToken); //TODO: String can be unsafe?
            try (var preparedStatement = conn.prepareStatement(statement)) {
                try (var result = preparedStatement.executeQuery()) {
                    if (result.next()) {
                        auth = gson.fromJson(result.getString("auth"), AuthData.class);
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
            var statement = "DELETE FROM auths WHERE authToken='%s'".formatted(authToken); //TODO: String can be unsafe?
            try (var preparedStatement = conn.prepareStatement(statement)) {
                preparedStatement.executeUpdate();
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public String getUsername(String authToken) throws DataAccessException {
        AuthData auth = getAuth(authToken);
        if (auth != null) {
            return auth.username();
        }
        throw new DataAccessException("Error: Unauthorized");
    }

    @Override
    public void clear() {
        try (var conn = DatabaseManager.getConnection()) {
            var statement = "TRUNCATE auths";
            try (var preparedStatement = conn.prepareStatement(statement)) {
                preparedStatement.executeUpdate();
            }
        } catch (SQLException | DataAccessException e) {
            throw new RuntimeException(e);
        }
    }
}
