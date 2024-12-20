package chess;

import java.util.Collection;
import java.util.HashSet;

public class KnightMoveCalculator implements PieceMoveCalculator {
    @Override
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        HashSet<ChessMove> moves = new HashSet<>();
        /*
        | |8| |1| |
        |7| | | |2|
        | | |N| | |
        |6| | | |3|
        | |5| |4| |
         */
        addMoveIfValid(board, moves, myPosition, 2, 1);
        addMoveIfValid(board, moves, myPosition, 1, 2);
        addMoveIfValid(board, moves, myPosition, -1, 2);
        addMoveIfValid(board, moves, myPosition, -2, 1);
        addMoveIfValid(board, moves, myPosition, -2, -1);
        addMoveIfValid(board, moves, myPosition, -1, -2);
        addMoveIfValid(board, moves, myPosition, 1, -2);
        addMoveIfValid(board, moves, myPosition, 2, -1);
        return moves;
    }

    protected void addMoveIfValid(ChessBoard board, HashSet<ChessMove> moves, ChessPosition position, int addRow, int addCol) {
        int newRow = position.getRow() + addRow;
        int newCol = position.getCol() + addCol;
        if (newRow > 8 || newRow < 1 || newCol > 8 || newCol < 1) {
            return;
        }
        ChessPosition newPos = new ChessPosition(newRow, newCol);
        ChessPiece atPos = board.getPiece(newPos);
        if (atPos != null) {
            if (atPos.getTeamColor() != board.getPiece(position).getTeamColor()) {
                moves.add(new ChessMove(position, newPos));
            }
        } else {
            moves.add(new ChessMove(position, newPos));
        }
    }
}
