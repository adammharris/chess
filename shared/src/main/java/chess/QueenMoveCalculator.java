package chess;

import java.util.Collection;

public class QueenMoveCalculator extends PieceMovesCalculator {
    @Override
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition position) {
        RookMoveCalculator r = new RookMoveCalculator();
        Collection<ChessMove> moves1 = r.pieceMoves(board, position);
        BishopMoveCalculator b = new BishopMoveCalculator();
        Collection<ChessMove> moves2 = b.pieceMoves(board, position);
        moves1.addAll(moves2);
        return moves1;
    }
}
