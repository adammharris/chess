package chess;

/**
 * Represents moving a chess piece on a chessboard
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessMove {
    private final ChessPosition start;
    private final ChessPosition end;
    private final ChessPiece.PieceType promotion;
    public ChessMove(ChessPosition start, ChessPosition end,
                     ChessPiece.PieceType promotionPiece) {
        this.start = start;
        this.end = end;
        this.promotion = promotionPiece;
    }

    public ChessMove(ChessPosition start, ChessPosition end) {
        this.start = start;
        this.end = end;
        this.promotion = null;
    }

    /**
     * @return ChessPosition of starting location
     */
    public ChessPosition getStartPosition() {
        return this.start;
    }

    /**
     * @return ChessPosition of ending location
     */
    public ChessPosition getEndPosition() {
        return this.end;
    }

    /**
     * Gets the type of piece to promote a pawn to if pawn promotion is part of this
     * chess move
     *
     * @return Type of piece to promote a pawn to, or null if no promotion
     */
    public ChessPiece.PieceType getPromotionPiece() {
        return this.promotion;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null) return false;
        if (this.getClass() != obj.getClass()) return false;
        ChessMove m = (ChessMove) obj;
        boolean isEqual = this.start.equals(m.start) && this.end.equals(m.end);
        if (m.promotion == null && this.promotion != null
                || m.promotion != null && this.promotion == null) {
            return false;
        } else if (m.promotion == null) return isEqual;
        return isEqual && this.promotion.equals(m.promotion);
    }

    @Override
    public String toString() {
        String s = "%s -> %s".formatted(this.start.toString(), this.end.toString());
        if (this.promotion != null) {
            s = s + " (Promotion: %s)".formatted(this.promotion);
        }
        return s;
    }

    @Override
    public int hashCode() {
        return this.toString().hashCode();
    }
}
