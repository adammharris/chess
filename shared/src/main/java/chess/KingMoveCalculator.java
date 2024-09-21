package chess;

import java.util.Collection;
import java.util.HashSet;

public class KingMoveCalculator extends PieceMovesCalculator {
    @Override
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition position) {
        HashSet<ChessMove> moves = new HashSet<>();
        HashSet<ChessPosition> around = new HashSet<>();
        /*  1,-1|1,0 |1,1
            0,-1| K  |0,1
           -1,-1|-1,0|-1,1  */
        HashSet<ChessPosition> aroundWithInvalidSpaces = new HashSet<>();
        aroundWithInvalidSpaces.add(translate(position, 0, 1)); // 1 (0,1)
        aroundWithInvalidSpaces.add(translate(position, 1, 1)); // 2 (1,1)
        aroundWithInvalidSpaces.add(translate(position, 1, 0)); // 3 (1,0)
        aroundWithInvalidSpaces.add(translate(position, 1, -1)); // 4 (1,-1)
        aroundWithInvalidSpaces.add(translate(position, 0, -1)); // 5 (0,-1)
        aroundWithInvalidSpaces.add(translate(position, -1, -1)); // 6 (-1,-1)
        aroundWithInvalidSpaces.add(translate(position, -1, 0)); // 7 (-1,0)
        aroundWithInvalidSpaces.add(translate(position, -1, 1)); // 8 (-1,1)
        for (ChessPosition i : aroundWithInvalidSpaces) {
            if (i != null) around.add(i);
        }

        for (ChessPosition i : around) {
            addIfPieceIsNotFriendly(board, position, i, moves);
        }
        return moves;
    }
    private ChessPosition translate(ChessPosition pos, int row, int col) {
        int newRow = pos.getRow() + row;
        int newCol = pos.getColumn() + col;
        if (newRow > 8 || newRow < 1 || newCol > 8 || newCol < 1) {
            return null;
        }
        return new ChessPosition(newRow, newCol);
    }
}
