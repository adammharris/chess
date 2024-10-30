package dataaccess;

import java.sql.SQLException;

public abstract class SqlDAO {
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
}
