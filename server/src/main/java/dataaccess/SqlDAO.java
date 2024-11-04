package dataaccess;

import com.google.gson.Gson;

import java.sql.SQLException;

public abstract class SqlDAO {
    protected static Gson gson = new Gson();
    protected static String tableName;

    static {
        try {
            DatabaseManager.createDatabase();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void clear() {
        try (var conn = DatabaseManager.getConnection()) {
            var statement = "TRUNCATE " + tableName; //TODO: String can be unsafe?
            try (var preparedStatement = conn.prepareStatement(statement)) {
                preparedStatement.executeUpdate();
            }
        } catch (SQLException | DataAccessException e) {
            throw new RuntimeException(e);
        }
    }

    public static void delete(String key, String value) throws DataAccessException {
        try (var conn = DatabaseManager.getConnection()) {
            var statement = "DELETE FROM %s WHERE %s='%s'".formatted(tableName, key, value); //TODO: String can be unsafe?
            try (var preparedStatement = conn.prepareStatement(statement)) {
                preparedStatement.executeUpdate();
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static void create(String[] keys, String[] values) {
        String statement = "INSERT INTO %s (".formatted(tableName);
        for (String key : keys) {
            statement = statement.concat(key + ", ");
        }
        statement = statement.substring(0, statement.length()-2).concat(") VALUES (");
        for (String value : values) {
            statement = statement.concat("'" + value + "', ");
        }
        statement = statement.substring(0, statement.length()-2).concat(")");
        // TODO: String can be unsafe?
        try (var conn = DatabaseManager.getConnection()) {
            try (var preparedStatement = conn.prepareStatement(statement)) {
                preparedStatement.executeUpdate();
            }
        } catch (SQLException | DataAccessException e) {
            throw new RuntimeException(e);
        }
    }

    public static <T> T get(String keyIdentifier, String valueIdentifier, String keyGet, Class<T> classOfT) throws DataAccessException {
        if (keyIdentifier == null) {
            throw new DataAccessException("Error: Bad request");
        }
        T rec = null;
        try (var conn = DatabaseManager.getConnection()) {
            var statement = "SELECT %s, %s FROM %s WHERE %s='%s'".formatted(keyIdentifier, keyGet, tableName, keyIdentifier, valueIdentifier);
            //TODO: String can be unsafe?
            try (var preparedStatement = conn.prepareStatement(statement)) {
                try (var result = preparedStatement.executeQuery()) {
                    if (result.next()) {
                        rec = gson.fromJson(result.getString(keyGet), classOfT);
                    }
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        if (rec == null) {
            throw new DataAccessException("Error: Not found");
        }
        return rec;
    }
}
