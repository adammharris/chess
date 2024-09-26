package chess;

import java.util.Collection;

interface PieceMoveCalculator {
    Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition);
}
