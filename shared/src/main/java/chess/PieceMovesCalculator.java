package chess;

import java.util.Collection;

abstract class PieceMovesCalculator {
    abstract Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition position);
    public void addIfPieceIsNotFriendly(ChessBoard board, ChessPosition position, ChessPosition newPosition, Collection<ChessMove> moves) {
        ChessPiece pieceOnNewPosition = board.getPiece(newPosition);
        if (pieceOnNewPosition != null) {
            ChessGame.TeamColor thisColor = board.getPiece(position).getTeamColor();
            ChessGame.TeamColor thatColor = pieceOnNewPosition.getTeamColor();
            if (thisColor != thatColor) {
                moves.add(new ChessMove(position, newPosition));
            }
        } else {
            moves.add(new ChessMove(position, newPosition));
        }
    }
}
