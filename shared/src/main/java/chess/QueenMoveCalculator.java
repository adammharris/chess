package chess;

import java.util.Collection;
import java.util.HashSet;

public class QueenMoveCalculator implements PieceMoveCalculator {
    @Override
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        RookMoveCalculator r = new RookMoveCalculator();
        BishopMoveCalculator b = new BishopMoveCalculator();
        Collection<ChessMove> rookMoves = r.pieceMoves(board, myPosition);
        Collection<ChessMove> bishopMoves = b.pieceMoves(board, myPosition);
        HashSet<ChessMove> moves = new HashSet<>();
        moves.addAll(rookMoves);
        moves.addAll(bishopMoves);
        return moves;
    }
}
