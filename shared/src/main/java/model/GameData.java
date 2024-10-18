package model;
import chess.ChessGame;

/**
 * Represents all the data related to a game being played on the chess server.
 * @param gameID Identifier for this specific game, to prevent duplicates.
 * @param whiteUsername Username of player for the white pieces
 * @param blackUsername Username of player for the black pieces
 * @param gameName Identifier used to join or list the game
 * @param game Object containing all logic and data related to the chess game
 */
public record GameData(
        int gameID,
        String whiteUsername,
        String blackUsername,
        String gameName,
        ChessGame game
) { }