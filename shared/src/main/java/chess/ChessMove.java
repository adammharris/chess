package chess;

/**
 * Represents moving a chess piece on a chessboard
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessMove {
    ChessPosition start;
    ChessPosition end;
    ChessPiece.PieceType promotion;
    public ChessMove(ChessPosition startPosition, ChessPosition endPosition,
                     ChessPiece.PieceType promotionPiece) {
        this.start = startPosition;
        this.end = endPosition;
        this.promotion = promotionPiece;
    }
    public ChessMove(ChessPosition startPosition, ChessPosition endPosition) {
        this.start = startPosition;
        this.end = endPosition;
        this.promotion = null;
    }

    /**
     * @return ChessPosition of starting location
     */
    public ChessPosition getStartPosition() {
        return start;
    }

    /**
     * @return ChessPosition of ending location
     */
    public ChessPosition getEndPosition() {
        return end;
    }

    @Override
    public String toString() {
        String moveString;
        if (promotion != null) {
            moveString = "Start: (%s), End: (%s), Promo: %s".formatted(start.toString(), end.toString(), promotion);
        } else {
            moveString = "Start: (%s), End: (%s)".formatted(start.toString(), end.toString());
        }
        return moveString;
    }

    @Override
    public int hashCode() {
        return this.toString().hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null) return false;
        if (this.getClass() != obj.getClass()) return false;
        ChessMove newMove = (ChessMove) obj;
        return this.hashCode() ==  newMove.hashCode();
    }

    /**
     * Gets the type of piece to promote a pawn to if pawn promotion is part of this
     * chess move
     *
     * @return Type of piece to promote a pawn to, or null if no promotion
     */
    public ChessPiece.PieceType getPromotionPiece() {
        return promotion;
    }
}
