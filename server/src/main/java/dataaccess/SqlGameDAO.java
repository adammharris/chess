package dataaccess;

import chess.ChessGame;
import model.GameData;
import model.UserData;

import java.sql.SQLException;
import java.util.HashSet;

public class SqlGameDAO extends SqlDAO implements GameDAO {
    private static SqlGameDAO instance;
    private SqlGameDAO() {
        setTable("games", "(gameName VARCHAR(128), gameID INT, game JSON)");
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
        GameData gameToAdd = new GameData(rand.nextInt(100000), null, null, gameName, new ChessGame());
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
        create(new String[]{"gameName", "gameID", "game"}, new String[]{gameName, "%s".formatted(gameToAdd.gameID()), gson.toJson(gameToAdd)});
        return gameToAdd;
    }

    @Override
    public GameData getGame(int gameID) throws DataAccessException {
        GameData game = get("gameID", "%s".formatted(gameID), "game", GameData.class);
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
        previousGame = getGame(updatedGame.gameID());
        create(new String[]{"game"}, new String[]{gson.toJson(updatedGame)});

        if (previousGame == null) {
            throw new DataAccessException("Error: Bad request");
        }
    }

    public GameData[] getGames() {
        java.sql.ResultSet games;
        HashSet<GameData> gameSet = new HashSet<>();
        try (var conn = DatabaseManager.getConnection()) {
            var statement = "SELECT game FROM games";
            try (var preparedStatement = conn.prepareStatement(statement)) {
                games = preparedStatement.executeQuery();
                while (games.next()) {
                    String gameJson = games.getString("game");
                    gameSet.add(gson.fromJson(gameJson, GameData.class));
                }
            }
        } catch (DataAccessException | SQLException e) {
            throw new RuntimeException(e);
        }
        GameData[] toReturn = {};
        gameSet.toArray(toReturn);
        return toReturn;
    }
}
