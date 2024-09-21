package chess;

import java.util.Collection;
import java.util.HashSet;

public class RookMoveCalculator implements PieceMovesCalculator {

    private enum direction {
        UP,
        DOWN,
        LEFT,
        RIGHT
    }

    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition position) {
        HashSet<ChessMove> moves = new HashSet<>();
        addRowMoves(board, position, moves, direction.LEFT);
        addRowMoves(board, position, moves, direction.RIGHT);
        addRowMoves(board, position, moves, direction.UP);
        addRowMoves(board, position, moves, direction.DOWN);
        return moves;
    }

    private void addRowMoves(ChessBoard board, ChessPosition position, HashSet<ChessMove> moves, direction d) {
        int loopRow = position.getRow();
        int loopCol = position.getColumn();
        ChessGame.TeamColor thisColor = board.getPiece(position).getTeamColor();
        switch (d) {
            case direction.LEFT:
                do {
                    loopRow--;
                    if (loopRow < 1) continue;
                    ChessPosition newPosition = new ChessPosition(loopRow, loopCol);
                    ChessPiece pieceOnNewPosition = board.getPiece(newPosition);
                    if (pieceOnNewPosition != null) {
                        ChessGame.TeamColor thatColor = pieceOnNewPosition.getTeamColor();
                        if (thisColor == thatColor) break;
                        moves.add(new ChessMove(position, newPosition));
                        break;
                    } else {
                        moves.add(new ChessMove(position, newPosition));
                    }
                } while (loopRow >= 1);
                break;
            case direction.RIGHT:
                do {
                    loopRow++;
                    if (loopRow > 8) continue;
                    ChessPosition newPosition = new ChessPosition(loopRow, loopCol);
                    ChessPiece pieceOnNewPosition = board.getPiece(newPosition);
                    if (pieceOnNewPosition != null) {
                        ChessGame.TeamColor thatColor = pieceOnNewPosition.getTeamColor();
                        if (thisColor == thatColor) break;
                        moves.add(new ChessMove(position, newPosition));
                        break;
                    } else {
                        moves.add(new ChessMove(position, newPosition));
                    }
                } while (loopRow <= 8);
                break;
            case direction.UP:
                do {
                    loopCol++;
                    if (loopCol > 8) continue;
                    ChessPosition newPosition = new ChessPosition(loopRow, loopCol);
                    ChessPiece pieceOnNewPosition = board.getPiece(newPosition);
                    if (pieceOnNewPosition != null) {
                        ChessGame.TeamColor thatColor = pieceOnNewPosition.getTeamColor();
                        if (thisColor == thatColor) break;
                        moves.add(new ChessMove(position, newPosition));
                        break;
                    } else {
                        moves.add(new ChessMove(position, newPosition));
                    }
                } while (loopCol <= 8);
                break;
            case direction.DOWN:
                do {
                    loopCol--;
                    if (loopCol < 1) continue;
                    ChessPosition newPosition = new ChessPosition(loopRow, loopCol);
                    ChessPiece pieceOnNewPosition = board.getPiece(newPosition);
                    if (pieceOnNewPosition != null) {
                        ChessGame.TeamColor thatColor = pieceOnNewPosition.getTeamColor();
                        if (thisColor == thatColor) break;
                        moves.add(new ChessMove(position, newPosition));
                        break;
                    } else {
                        moves.add(new ChessMove(position, newPosition));
                    }
                } while (loopCol >= 1);
                break;
        }
    }
}
