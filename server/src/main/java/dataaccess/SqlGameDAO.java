package dataaccess;

import chess.ChessGame;
import model.GameData;

import java.sql.SQLException;
import java.util.ArrayList;

public class SqlGameDAO extends SqlDAO implements GameDAO {
    private static SqlGameDAO instance;
    private SqlGameDAO() {
        setTable("games", "(gameName VARCHAR(128), gameID INT, whiteUsername VARCHAR(128), blackUsername VARCHAR(128), game JSON)");
    }

    public static SqlGameDAO getInstance() {
        if (instance == null) {
            instance = new SqlGameDAO();
        }
        return instance;
    }

    @Override
    public GameData createGame(String gameName) throws DataAccessException {
        java.util.Random rand = new java.util.Random();
        GameData currentGame = null;
        try {
            currentGame = get("gameName", gameName, "game", GameData.class);
        } catch (DataAccessException e) {
            if (!e.getMessage().contains("Unauthorized")) {
                throw new RuntimeException(e);
            }
        }
        if (currentGame != null) {
            throw new DataAccessException("Error: Bad request");
        }
        GameData gameToAdd = new GameData(rand.nextInt(100000, 999999), null, null, gameName, new ChessGame());
        create(new String[]{"gameName", "gameID", "whiteUsername", "blackUsername", "game"},
                new String[]{gameName, "%s".formatted(gameToAdd.gameID()), "null", "null", gson.toJson(gameToAdd)});
        return gameToAdd;
    }

    @Override
    public GameData getGame(int gameID) throws DataAccessException {
        GameData game = null;

        try (var conn = DatabaseManager.getConnection()) {
            var statement = "SELECT * FROM games WHERE gameID = ?";
            try (var preparedStatement = conn.prepareStatement(statement)) {
                preparedStatement.setInt(1, gameID);
                try (var resultSet = preparedStatement.executeQuery()) {
                    if (resultSet.next()) {
                        String gameName = resultSet.getString("gameName");
                        String whiteUsername = resultSet.getString("whiteUsername");
                        String blackUsername = resultSet.getString("blackUsername");
                        String gameJson = resultSet.getString("game");
                        ChessGame chessGame = gson.fromJson(gameJson, ChessGame.class);
                        game = new GameData(gameID, whiteUsername, blackUsername, gameName, chessGame);
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
        GameData previousGame = getGame(game.gameID());
        if (previousGame == null) {
            throw new DataAccessException("Error: Bad request");
        }

        String newName = game.gameName();
        if (newName == null) {
            newName = previousGame.gameName();
        }

        String newWhite = game.whiteUsername();
        if (newWhite == null) {
            newWhite = previousGame.whiteUsername();
        }

        String newBlack = game.blackUsername();
        if (newBlack == null) {
            newBlack = previousGame.blackUsername();
        }

        ChessGame newGame = game.game();
        if (newGame == null) {
            newGame = previousGame.game();
        }

        //GameData finalGame = new GameData(game.gameID(), newWhite, newBlack, newName, newGame);
        //delete("gameID", "%s".formatted(game.gameID()));
        set("whiteUsername", newWhite, "gameID", "%s".formatted(game.gameID()));
        set("blackUsername", newBlack, "gameID", "%s".formatted(game.gameID()));
        //set("gameName", newName, "gameID", "%s".formatted(game.gameID()));
        set("game", gson.toJson(newGame), "gameID", "%s".formatted(game.gameID()));
    }

    public GameData[] getGames() {
        //java.sql.ResultSet games;
        ArrayList<GameData> gameList = new ArrayList<>();
        try (var conn = DatabaseManager.getConnection()) {
            var statement = "SELECT * FROM games";
            try (var preparedStatement = conn.prepareStatement(statement)) {
                try (var resultSet = preparedStatement.executeQuery()) {
                    while (resultSet.next()) {
                        int gameID = resultSet.getInt("gameID");
                        String gameName = resultSet.getString("gameName");
                        String whiteUsername = resultSet.getString("whiteUsername");
                        String blackUsername = resultSet.getString("blackUsername");
                        String gameJson = resultSet.getString("game");
                        ChessGame chessGame = gson.fromJson(gameJson, ChessGame.class);
                        GameData gameData = new GameData(gameID, whiteUsername, blackUsername, gameName, chessGame);
                        gameList.add(gameData);
                    }
                }
            }
        } catch (DataAccessException | SQLException e) {
            throw new RuntimeException(e);
        }
        return gameList.toArray(new GameData[0]);
    }
}
