package dataaccess;

import chess.ChessGame;
import model.GameData;

import java.sql.SQLException;
import java.util.HashMap;

//TODO: abstract SQL access into SqlDao
public class SqlGameDAO extends SqlDAO implements GameDAO {
    private static SqlGameDAO instance;
    private SqlGameDAO() {}

    public static SqlGameDAO getInstance() {
        if (instance == null) {
            instance = new SqlGameDAO();
        }
        return instance;
    }

    @Override
    public GameData createGame(String gameName) throws DataAccessException {
        java.util.Random rand = new java.util.Random();
        GameData gameToAdd = new GameData(rand.nextInt(100000), null, null, gameName, new ChessGame());
        try (var conn = DatabaseManager.getConnection()) {
            var game = gson.toJson(gameToAdd);
            var statement = "INSERT INTO chess (gameName, game) VALUES (%s, %s)".formatted(gameName, game);
            try (var preparedStatement = conn.prepareStatement(statement)) {
                preparedStatement.executeQuery();
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return gameToAdd;
    }

    @Override
    public GameData getGame(int gameID) throws DataAccessException {
        GameData game = null;
        try (var conn = DatabaseManager.getConnection()) {
            var statement = "SELECT gameID, game FROM chess WHERE gameID=%s".formatted(gameID);
            try (var preparedStatement = conn.prepareStatement(statement)) {
                try (var result = preparedStatement.executeQuery()) {
                    if (result.next()) {
                        game = gson.fromJson(result.getString("game"), GameData.class);
                    }
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        if (game == null) {
            throw new DataAccessException("Error: Bad request");
        }
        return game;
    }

    public void updateGame(GameData game) throws DataAccessException {
        GameData updatedGame = null;
        getGame(game.gameID());
        SqlAuthDAO authDAO = SqlAuthDAO.getInstance();
        if (game.whiteUsername() != null) {
            if (game.whiteUsername().length() == 36) {
                updatedGame = new GameData(game.gameID(),
                        authDAO.getUsername(game.whiteUsername()),
                        game.blackUsername(),
                        game.gameName(),
                        game.game());
            } else if (game.blackUsername() != null) {
                if (game.blackUsername().length() == 36) {
                    updatedGame = new GameData(game.gameID(),
                            game.whiteUsername(),
                            authDAO.getUsername(game.blackUsername()),
                            game.gameName(),
                            game.game());
                }
            }
        }
        GameData previousGame = null;
        if (updatedGame == null) {
            throw new DataAccessException("Error: Bad request");
        }

        try (var conn = DatabaseManager.getConnection()) {
            var statement = "INSERT INTO chess (gameName, gameID, game) VALUES (%s, %s, %s)".formatted(
                    updatedGame.gameName(),
                    updatedGame.gameID(),
                    updatedGame
            );
            try (var preparedStatement = conn.prepareStatement(statement)) {
                preparedStatement.executeQuery();
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        //TODO: previousGame = games.put(game.gameID(), Objects.requireNonNullElse(updatedGame, game));

        if (previousGame == null) {
            throw new DataAccessException("Error: Bad request");
        }
    }

    public HashMap<Integer, GameData> getGames() {
        java.sql.ResultSet games;
        try (var conn = DatabaseManager.getConnection()) {
            var statement = "SELECT game FROM chess";
            try (var preparedStatement = conn.prepareStatement(statement)) {
                games = preparedStatement.executeQuery();
            }
        } catch (DataAccessException | SQLException e) {
            throw new RuntimeException(e);
        }
        //TODO: create Hashmap from ResultSet

        return new HashMap<>();
    }
}
