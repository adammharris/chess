package chess;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;

/**
 * For a class that can manage a chess game, making moves on a board
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessGame {
    //public int turnNum = 0;
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
        ChessPiece startPiece = board.getPiece(startPosition);
        if (startPiece == null) {
            return new HashSet<>();
        }
        Collection<ChessMove> moves = startPiece.pieceMoves(board, startPosition);
        TeamColor myTeamColor = board.getPiece(startPosition).getTeamColor();
        Iterator<ChessMove> iterator = moves.iterator();
        while (iterator.hasNext()) {
            ChessMove move = iterator.next();
            ChessPosition start = new ChessPosition(move.getStartPosition().getRow(), move.getStartPosition().getCol());
            ChessPosition end = new ChessPosition(move.getEndPosition().getRow(), move.getEndPosition().getCol());
            ChessBoard simulateCheck = new ChessBoard(board);
            simulateCheck.movePiece(start, end);
            boolean afterInCheck = isInCheck(myTeamColor, simulateCheck);
            if (afterInCheck) {
                iterator.remove();
            }
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
        ChessPosition startPosition = new ChessPosition(move.getStartPosition().getRow(), move.getStartPosition().getCol());
        ChessPosition endPosition = new ChessPosition(move.getEndPosition().getRow(), move.getEndPosition().getCol());
        ChessPiece thisPiece = board.getPiece(new ChessPosition(startPosition.getRow(), startPosition.getCol()));
        if (thisPiece == null) {
            throw new InvalidMoveException("Piece is null");
        }

        if (!(ChessPosition.isValid(startPosition) && ChessPosition.isValid(endPosition))) {
            throw new InvalidMoveException("Move position is invalid");
        }

        TeamColor thisColor = thisPiece.getTeamColor();
        if (thisColor != currentTurn) {
            throw new InvalidMoveException("Cannot move out of turn");
        }

        Collection<ChessMove> validMoves = validMoves(startPosition);
        boolean moveIsInValidMoves = false;
        for (ChessMove vm : validMoves) {
            if (move.equals(vm)) {
                moveIsInValidMoves = true;
                break;
            }
        }

        if (moveIsInValidMoves) {
            board.executeMove(move);
        } else {
            throw new InvalidMoveException();
        }

        // Change turn
        if (currentTurn == TeamColor.BLACK) {
            currentTurn = TeamColor.WHITE;
        } else {
            currentTurn = TeamColor.BLACK;
        }
    }

    /**
     * Determines if the given team is in check
     *
     * @param teamColor which team to check for check
     * @return True if the specified team is in check
     */
    public boolean isInCheck(TeamColor teamColor) {
        ChessGameState state = checkGameState(teamColor, this.board, ChessGameState.CHECK);
        return state == ChessGameState.CHECK;
    }
    public boolean isInCheck(TeamColor teamColor, ChessBoard board) {
        ChessGameState state = checkGameState(teamColor, board, ChessGameState.CHECK);
        return state == ChessGameState.CHECK;
    }

    private enum ChessGameState {
        CHECK,
        CHECKMATE,
        STALEMATE,
        NORMAL
    }

    private ChessGameState checkGameState(TeamColor teamColor, ChessBoard board, ChessGameState checkFor) {
        // First, see if the team is in check
        ChessPiece[] pieces = board.getPieces();
        boolean isInCheck = false;
        ChessPosition kingPos = board.getPosition(teamColor, ChessPiece.PieceType.KING);
        for (ChessPiece chessPiece : pieces) {
            if (chessPiece.getTeamColor() == teamColor) {
                continue;
            }
            Collection<ChessMove> moves = chessPiece.pieceMoves(board, chessPiece.position);
            for (ChessMove move : moves) {
                if (move.getEndPosition().equals(kingPos)) {
                    isInCheck = true;
                    break;
                }
            }
            if (isInCheck) {
                break;
            }
        }

        if (checkFor == ChessGameState.CHECK) {
            return (isInCheck) ? ChessGameState.CHECK : ChessGameState.NORMAL;
        }

        // Second, check for Checkmate and Stalemate
        boolean isCheckmate = true;
        boolean isStalemate = true;
        for (ChessPiece piece : pieces) {
            if (piece.getTeamColor() != teamColor) {
                continue;
            }
            Collection<ChessMove> moves = validMoves(piece.position);
            for (ChessMove move : moves) {
                ChessBoard simulateCheck = new ChessBoard(board);
                simulateCheck.executeMove(move);
                if (!isInCheck(teamColor, simulateCheck)) {
                    isStalemate = false;
                    isCheckmate = false;
                    break;
                }
            }
            if (!isCheckmate) {
                break;
            }
        }

        if (checkFor == ChessGameState.CHECKMATE) {
            return (isCheckmate && isInCheck) ? ChessGameState.CHECKMATE : ChessGameState.NORMAL;
        } else if (checkFor == ChessGameState.STALEMATE) {
            return (isStalemate && !isInCheck) ? ChessGameState.STALEMATE : ChessGameState.NORMAL;
        }
        return ChessGameState.NORMAL;
    }

    /**
     * Determines if the given team is in checkmate
     *
     * @param teamColor which team to check for checkmate
     * @return True if the specified team is in checkmate
     */
    public boolean isInCheckmate(TeamColor teamColor) {
        ChessGameState state = checkGameState(teamColor, this.board, ChessGameState.CHECKMATE);
        return state == ChessGameState.CHECKMATE;
    }

    /**
     * Determines if the given team is in stalemate, which here is defined as having
     * no valid moves
     *
     * @param teamColor which team to check for stalemate
     * @return True if the specified team is in stalemate, otherwise false
     */
    public boolean isInStalemate(TeamColor teamColor) {
        ChessGameState state = checkGameState(teamColor, this.board, ChessGameState.STALEMATE);
        return state == ChessGameState.STALEMATE;
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
