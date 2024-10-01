package chess;

import java.util.Collection;
import java.util.Iterator;

/**
 * For a class that can manage a chess game, making moves on a board
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessGame {
    public int turnNum = 0;
    private TeamColor currentTurn = TeamColor.WHITE;
    private ChessBoard board = new ChessBoard();
    ChessPosition kingPos;
    public ChessGame() {
        board.resetBoard();
        kingPos = board.getPosition(currentTurn, ChessPiece.PieceType.KING);
    }

    /**
     * @return Which team's turn it is
     */
    public TeamColor getTeamTurn() {
        return currentTurn;
    }

    /**
     * Set's which teams turn it is
     *
     * @param team the team whose turn it is
     */
    public void setTeamTurn(TeamColor team) {
        currentTurn = team;
    }

    /**
     * Enum identifying the 2 possible teams in a chess game
     */
    public enum TeamColor {
        WHITE,
        BLACK
    }


    /**
     * Gets a valid moves for a piece at the given location
     *
     * @param startPosition the piece to get valid moves for
     * @return Set of valid moves for requested piece, or null if no piece at
     * startPosition
     */
    public Collection<ChessMove> validMoves(ChessPosition startPosition) {
        Collection<ChessMove> moves = board.getPiece(startPosition).pieceMoves(board, startPosition);
        TeamColor myTeamColor = board.getPiece(startPosition).getTeamColor();
        Iterator<ChessMove> iterator = moves.iterator();
        while (iterator.hasNext()) {
            ChessMove move = iterator.next();
            ChessBoard simulateCheck = new ChessBoard(board);
            simulateCheck.movePiece(move.getStartPosition(), move.getEndPosition());
            boolean afterInCheck = isInCheck(myTeamColor, simulateCheck);
            if (afterInCheck) iterator.remove();
        }
        return moves;
    }

    /**
     * Makes a move in a chess game. Checks if move is valid.
     *
     * @param move chess move to preform
     * @throws InvalidMoveException if move is invalid
     */
    public void makeMove(ChessMove move) throws InvalidMoveException {
        /*
        1. Check if piece is not occupied by a piece of the same team (unless castling)
        2. Check if my team is in check. If so, only allow moves that protect the king
        3.
         */

        ChessPiece attackedPiece = board.getPiece(move.getEndPosition());
        TeamColor myTeamColor = board.getPiece(move.getStartPosition()).getTeamColor();

        if (attackedPiece != null) {
            if (attackedPiece.getTeamColor() == myTeamColor) {
                throw new InvalidMoveException("Move ends on piece of same color");
            }
        }

        boolean beforeInCheck = isInCheck(myTeamColor, this.board);
        ChessBoard simulateCheck = new ChessBoard(board);
        simulateCheck.movePiece(move.getStartPosition(), move.getEndPosition());
        boolean afterInCheck = isInCheck(myTeamColor, simulateCheck);
        if (beforeInCheck && !afterInCheck) throw new InvalidMoveException("King is in check");
    }

    /**
     * Determines if the given team is in check
     *
     * @param teamColor which team to check for check
     * @return True if the specified team is in check
     */
    public boolean isInCheck(TeamColor teamColor) {
        java.util.HashMap<ChessPosition, ChessPiece> pieces = board.getPieces();
        ChessPosition kingPos = board.getPosition(teamColor, ChessPiece.PieceType.KING);
        for (java.util.Map.Entry<ChessPosition, ChessPiece> piece : pieces.entrySet()) {
            if (piece.getValue().getTeamColor() == teamColor) continue;
            Collection<ChessMove> moves = piece.getValue().pieceMoves(board, piece.getKey());
            for (ChessMove move : moves) {
                if (move.getEndPosition().equals(kingPos)) return true;
            }
        }
        return false;
    }

    public static boolean isInCheck(TeamColor teamColor, ChessBoard board) {
        java.util.HashMap<ChessPosition, ChessPiece> pieces = board.getPieces();
        ChessPosition kingPos = board.getPosition(teamColor, ChessPiece.PieceType.KING);
        for (java.util.Map.Entry<ChessPosition, ChessPiece> piece : pieces.entrySet()) {
            if (piece.getValue().getTeamColor() == teamColor) continue;
            Collection<ChessMove> moves = piece.getValue().pieceMoves(board, piece.getKey());
            for (ChessMove move : moves) {
                if (move.getEndPosition().equals(kingPos)) return true;
            }
        }
        return false;
    }

    /**
     * Determines if the given team is in checkmate
     *
     * @param teamColor which team to check for checkmate
     * @return True if the specified team is in checkmate
     */
    public boolean isInCheckmate(TeamColor teamColor) {
        throw new RuntimeException("Not implemented");
    }

    /**
     * Determines if the given team is in stalemate, which here is defined as having
     * no valid moves
     *
     * @param teamColor which team to check for stalemate
     * @return True if the specified team is in stalemate, otherwise false
     */
    public boolean isInStalemate(TeamColor teamColor) {
        throw new RuntimeException("Not implemented");
    }

    /**
     * Sets this game's chessboard with a given board
     *
     * @param board the new board to use
     */
    public void setBoard(ChessBoard board) {
        this.board = board;
    }

    /**
     * Gets the current chessboard
     *
     * @return the chessboard
     */
    public ChessBoard getBoard() {
        return board;
    }
}
