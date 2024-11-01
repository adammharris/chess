package dataaccess;

import com.google.gson.Gson;

import java.sql.SQLException;

public abstract class SqlDAO {
    protected static Gson gson = new Gson();

    static {
        try {
            DatabaseManager.createDatabase();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void clear() {
        try (var conn = DatabaseManager.getConnection()) {
            var statement = "TRUNCATE chess";
            try (var preparedStatement = conn.prepareStatement(statement)) {
                preparedStatement.executeQuery();
            }
        } catch (SQLException | DataAccessException e) {
            throw new RuntimeException(e);
        }
    }

    // TODO: abstract SQL access


}
