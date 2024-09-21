package chess;

import java.util.Collection;

/**
 * Represents a single chess piece
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessPiece {
    private PieceType type;
    private final ChessGame.TeamColor color;

    public ChessPiece(ChessGame.TeamColor pieceColor, ChessPiece.PieceType type) {
        this.color = pieceColor;
        this.type = type;
        switch (type) {
            case ROOK:
            case BISHOP:
            case QUEEN:
            case KING:
            case PAWN:
            case KNIGHT:
        }
    }

    /**
     * The various different chess piece options
     */
    public enum PieceType {
        KING,
        QUEEN,
        BISHOP,
        KNIGHT,
        ROOK,
        PAWN
    }

    /**
     * @return Which team this chess piece belongs to
     */
    public ChessGame.TeamColor getTeamColor() {
        return this.color;
    }

    /**
     * @return which type of chess piece this piece is
     */
    public PieceType getPieceType() {
        return this.type;
    }

    /**
     * Calculates all the positions a chess piece can move to
     * Does not take into account moves that are illegal due to leaving the king in
     * danger
     *
     * @return Collection of valid moves
     */
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition position) {
        switch (this.getPieceType()) {
            case ROOK:
                RookMoveCalculator c = new RookMoveCalculator();
                return c.pieceMoves(board, position);
        }
        return new java.util.HashSet<>();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null) return false;
        if (this.getClass() != obj.getClass()) return false;
        ChessPiece p = (ChessPiece) obj;
        //System.out.println("%s %s %s %s".formatted(p.getPieceType().toString(), this.getPieceType().toString(), p.getTeamColor().toString(), this.getTeamColor().toString()));
        return (p.getPieceType() == this.getPieceType()) && (p.getTeamColor() == this.getTeamColor());
    }
    @Override
    public String toString() {
        if (this.color == ChessGame.TeamColor.BLACK) {
            return switch (this.type) {
                case PieceType.KING -> "K";
                case PieceType.QUEEN -> "Q";
                case PieceType.BISHOP -> "B";
                case PieceType.KNIGHT -> "N";
                case PieceType.ROOK -> "R";
                case PieceType.PAWN -> "P";
            };
        } else if (this.color == ChessGame.TeamColor.WHITE) {
            return switch (this.type) {
                case PieceType.KING -> "k";
                case PieceType.QUEEN -> "q";
                case PieceType.BISHOP -> "b";
                case PieceType.KNIGHT -> "n";
                case PieceType.ROOK -> "r";
                case PieceType.PAWN -> "p";
            };
        }
        return "";
    }
}