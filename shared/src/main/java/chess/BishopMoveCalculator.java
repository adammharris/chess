package chess;

import java.util.Collection;
import java.util.HashSet;

public class BishopMoveCalculator extends PieceMovesCalculator {
    enum direction {
        UP_LEFT,
        UP_RIGHT,
        DOWN_LEFT,
        DOWN_RIGHT
    }

    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition position) {
        HashSet<ChessMove> moves = new HashSet<>();
        addDiagonalMoves(board, position, moves, direction.UP_RIGHT);
        addDiagonalMoves(board, position, moves, direction.UP_LEFT);
        addDiagonalMoves(board, position, moves, direction.DOWN_LEFT);
        addDiagonalMoves(board, position, moves, direction.DOWN_RIGHT);
        return moves;
    }

    private void addDiagonalMoves(ChessBoard board, ChessPosition position, HashSet<ChessMove> moves, direction d) {
        int loopRow = position.getRow();
        int loopCol = position.getColumn();
        ChessGame.TeamColor thisColor = board.getPiece(position).getTeamColor();
        //To do: less duplicated code
        switch (d) {
            case direction.UP_LEFT:
                do {
                    loopRow--;
                    loopCol++;
                    if (loopRow < 1 || loopCol > 8) continue;
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
                } while (loopRow >= 1 && loopCol <= 8);
                break;
            case UP_RIGHT:
                do {
                    loopRow++;
                    loopCol++;
                    if (loopRow > 8 || loopCol > 8) continue;
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
                } while (loopRow <= 8 && loopCol <= 8);
                break;
            case DOWN_LEFT:
                do {
                    loopRow--;
                    loopCol--;
                    if (loopRow < 1 || loopCol < 1) continue;
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
                } while (loopCol >= 1 && loopRow >= 1);
                break;
            case DOWN_RIGHT:
                do {
                    loopRow++;
                    loopCol--;
                    if (loopRow > 8 || loopCol < 1) continue;
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
                } while (loopRow <= 8 && loopCol >= 1);
                break;
        }
    }
}
