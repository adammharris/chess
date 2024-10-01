package chess;

import java.util.Collection;
import java.util.HashSet;

/**
 * Represents a single chess piece
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessPiece {
    private final ChessGame.TeamColor color;
    private final ChessPiece.PieceType type;
    public ChessPiece(ChessGame.TeamColor pieceColor, ChessPiece.PieceType type) {
        this.color = pieceColor;
        this.type = type;
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

    @Override
    public String toString() {
        if (this.color == ChessGame.TeamColor.WHITE) {
            return switch (this.type) {
                case KING -> "K";
                case QUEEN -> "Q";
                case BISHOP -> "B";
                case KNIGHT -> "N";
                case ROOK -> "R";
                case PAWN -> "P";
            };
        }
        return switch (this.type) {
            case KING -> "k";
            case QUEEN -> "q";
            case BISHOP -> "b";
            case KNIGHT -> "n";
            case ROOK -> "r";
            case PAWN -> "p";
        };

    }

    /**
     * Calculates all the positions a chess piece can move to
     * Does not take into account moves that are illegal due to leaving the king in
     * danger
     *
     * @return Collection of valid moves
     */
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        ChessPiece thisPiece = board.getPiece(myPosition);
        if (thisPiece == null) return new HashSet<>();
        ChessPiece.PieceType currentPiece = thisPiece.getPieceType();
        //HashSet<ChessMove> moves = new HashSet<>();
        return switch (currentPiece) {
            case KING -> {
                KingMoveCalculator k = new KingMoveCalculator();
                yield k.pieceMoves(board, myPosition);
            }
            case QUEEN -> {
                QueenMoveCalculator q = new QueenMoveCalculator();
                yield q.pieceMoves(board, myPosition);
            }
            case BISHOP -> {
                BishopMoveCalculator b = new BishopMoveCalculator();
                yield b.pieceMoves(board, myPosition);
            }
            case KNIGHT -> {
                KnightMoveCalculator n = new KnightMoveCalculator();
                yield n.pieceMoves(board, myPosition);
            }
            case ROOK -> {
                RookMoveCalculator r = new RookMoveCalculator();
                yield r.pieceMoves(board, myPosition);
            }
            case PAWN -> {
                PawnMoveCalculator p = new PawnMoveCalculator();
                yield p.pieceMoves(board, myPosition);
            }
        };
    }
}
