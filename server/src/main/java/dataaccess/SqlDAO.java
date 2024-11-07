package dataaccess;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import java.sql.SQLException;

public abstract class SqlDAO {
    protected static Gson gson = new Gson();
    protected String table;

    static {
        try {
            DatabaseManager.createDatabase();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    protected void setTable(String tn, String sqlTableDescription) {
        table = tn;
        try (var conn = DatabaseManager.getConnection()) {
            var statement = "CREATE TABLE IF NOT EXISTS %s %s".formatted(table, sqlTableDescription);
            try (var preparedStatement = conn.prepareStatement(statement)) {
                preparedStatement.executeUpdate();
            }
        } catch (SQLException | DataAccessException e) {
            throw new RuntimeException(e);
        }
    }

    public void clear() {
        try (var conn = DatabaseManager.getConnection()) {
            var statement = "TRUNCATE " + table; //TODO: String can be unsafe?
            try (var preparedStatement = conn.prepareStatement(statement)) {
                preparedStatement.executeUpdate();
            }
        } catch (SQLException | DataAccessException e) {
            throw new RuntimeException(e);
        }
    }

    protected void delete(String key, String value) throws DataAccessException {
        try (var conn = DatabaseManager.getConnection()) {
            var statement = "DELETE FROM %s WHERE %s='%s'".formatted(table, key, value); //TODO: String can be unsafe?
            try (var preparedStatement = conn.prepareStatement(statement)) {
                preparedStatement.executeUpdate();
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    protected void set(String column, String value, String conditionKey, String conditionValue) throws DataAccessException {
        try (var conn = DatabaseManager.getConnection()) {
            var statement = "UPDATE %s SET %s = '%s' WHERE %s = %s".formatted(table, column, value, conditionKey, conditionValue);
            try (var preparedStatement = conn.prepareStatement(statement)) {
                preparedStatement.executeUpdate();
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    protected void create(String[] keys, String[] values) throws DataAccessException {
        String statement = "INSERT INTO %s (".formatted(table);
        for (String key : keys) {
            statement = statement.concat(key + ", ");
        }
        statement = statement.substring(0, statement.length()-2).concat(") VALUES (");
        for (String value : values) {
            if (value == null) {
                throw new DataAccessException("Error: Bad request");
            }
            statement = statement.concat("'" + value + "', ");
        }
        statement = statement.substring(0, statement.length()-2).concat(")");
        // TODO: String can be unsafe?
        try (var conn = DatabaseManager.getConnection()) {
            try (var preparedStatement = conn.prepareStatement(statement)) {
                preparedStatement.executeUpdate();
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private boolean isInteger(String str) {
        try {
            Integer.parseInt(str);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }
    protected <T> T get(String keyIdentifier, String valueIdentifier, String keyGet, Class<T> classOfT) throws DataAccessException {
        if (keyIdentifier == null) {
            throw new DataAccessException("Error: Bad request");
        }
        T rec = null;
        try (var conn = DatabaseManager.getConnection()) {
            String statement;
            if (isInteger(valueIdentifier)) {
                statement = "SELECT %s, %s FROM %s WHERE %s=%s".formatted(keyIdentifier, keyGet, table, keyIdentifier, Integer.parseInt(valueIdentifier));
            } else {
                statement = "SELECT %s, %s FROM %s WHERE %s='%s'".formatted(keyIdentifier, keyGet, table, keyIdentifier, valueIdentifier);
            }

            //TODO: String can be unsafe?
            try (var preparedStatement = conn.prepareStatement(statement)) {
                try (var result = preparedStatement.executeQuery()) {
                    if (result.next()) {
                        rec = gson.fromJson(result.getString(keyGet), classOfT);
                    }
                } catch (JsonSyntaxException e) {
                    throw new RuntimeException(e);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        if (rec == null) {
            throw new DataAccessException("Error: Unauthorized");
        }
        return rec;
    }
}
