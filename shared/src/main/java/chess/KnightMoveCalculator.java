package chess;

import java.util.Collection;
import java.util.HashSet;

public class KnightMoveCalculator extends PieceMovesCalculator {
    @Override
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition position) {
        /*
          | |8| |1| |
          |7| | | |2|
          | | |N| | |
          |6| | | |3|
          | |5| |4| |
         */
        HashSet<ChessPosition> possiblePositions = new HashSet<>();
        int row = position.getRow();
        int col = position.getColumn();
        row += 2; col += 1;
        addIfWithinBounds(row, col, possiblePositions); // 1
        row -= 1; col += 1;
        addIfWithinBounds(row, col, possiblePositions); // 2
        row -= 2;
        addIfWithinBounds(row, col, possiblePositions); // 3
        row -= 1; col -= 1;
        addIfWithinBounds(row, col, possiblePositions); // 4
        col -= 2;
        addIfWithinBounds(row, col, possiblePositions); // 5
        row += 1; col -= 1;
        addIfWithinBounds(row, col, possiblePositions); // 6
        row += 2;
        addIfWithinBounds(row, col, possiblePositions);
        row += 1; col += 1;
        addIfWithinBounds(row, col, possiblePositions);

        HashSet<ChessMove> moves = new HashSet<>();
        for (ChessPosition newPosition : possiblePositions) {
            addIfPieceIsNotFriendly(board, position, newPosition, moves);
        }
        return moves;
    }

    private void addIfWithinBounds(int row, int col, HashSet<ChessPosition> positions) {
        if (row >= 1 && row <= 8 && col >= 1 && col <= 8) {
            positions.add(new ChessPosition(row, col));
        }
    }
}
