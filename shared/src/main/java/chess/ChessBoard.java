package chess;
import java.util.HashMap;
/**
 * A chessboard that can hold and rearrange chess pieces.
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessBoard {
    private HashMap<ChessPosition, ChessPiece> pieces = new HashMap<>();
    private static HashMap<ChessPosition, ChessPiece> defaultBoard = new HashMap<>();
    public ChessBoard() {
        if (defaultBoard.isEmpty()) {
            defineDefault();
        }
    }

    // Used to simulate moves
    public ChessBoard(ChessBoard board) {
        this.pieces.putAll(board.getPieces());
    }


    private void defineDefault() {
        defaultBoard.put(new ChessPosition(1,1), new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.ROOK));
        defaultBoard.put(new ChessPosition(1,2), new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.KNIGHT));
        defaultBoard.put(new ChessPosition(1,3), new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.BISHOP));
        defaultBoard.put(new ChessPosition(1,4), new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.QUEEN));
        defaultBoard.put(new ChessPosition(1,5), new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.KING));
        defaultBoard.put(new ChessPosition(1,6), new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.BISHOP));
        defaultBoard.put(new ChessPosition(1,7), new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.KNIGHT));
        defaultBoard.put(new ChessPosition(1,8), new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.ROOK));
        for (int i = 1; i <= 8; i++) {
            defaultBoard.put(new ChessPosition(2,i), new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.PAWN));
        }

        for (int i = 1; i <= 8; i++) {
            defaultBoard.put(new ChessPosition(7,i), new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.PAWN));
        }
        defaultBoard.put(new ChessPosition(8,1), new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.ROOK));
        defaultBoard.put(new ChessPosition(8,2), new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.KNIGHT));
        defaultBoard.put(new ChessPosition(8,3), new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.BISHOP));
        defaultBoard.put(new ChessPosition(8,4), new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.QUEEN));
        defaultBoard.put(new ChessPosition(8,5), new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.KING));
        defaultBoard.put(new ChessPosition(8,6), new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.BISHOP));
        defaultBoard.put(new ChessPosition(8,7), new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.KNIGHT));
        defaultBoard.put(new ChessPosition(8,8), new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.ROOK));

    }

    /**
     * Adds a chess piece to the chessboard
     *
     * @param position where to add the piece to
     * @param piece    the piece to add
     * @return previous ChessPiece as returned by HashMap.put
     */
    public ChessPiece addPiece(ChessPosition position, ChessPiece piece) {
        return pieces.put(position, piece);
    }

    /**
     * Gets a chess piece on the chessboard
     *
     * @param position The position to get the piece from
     * @return Either the piece at the position, or null if no piece is at that
     * position
     */
    public ChessPiece getPiece(ChessPosition position) {
        return pieces.get(position);
    }

    /**
     * Removes a chess piece from the chess board
     *
     * @param position position of piece to be removed
     * @return ChessPiece piece that is removed, as returned by HashMap<>.remove()
     */
    public ChessPiece removePiece(ChessPosition position) {
        return pieces.remove(position);
    }
    /**
     * Moves a chess piece from one position to another
     *
     * @param oldPos Original position of chess piece to be moved. Uses getPiece
     * @param newPos New position of chess piece to be moved. Uses removePiece and addPiece
     */
    public ChessPiece movePiece(ChessPosition oldPos, ChessPosition newPos) {
        return addPiece(newPos, removePiece(oldPos));
    }

    /**
     * Gets chess piece position based on color and type. Used for getting king in checking for check
     * @param color Color of chess piece to get
     * @param type Type of chess piece to get.
     * @return First piece found of color and type given
     */
    public ChessPosition getPosition(ChessGame.TeamColor color, ChessPiece.PieceType type) {
        for (java.util.Map.Entry<ChessPosition, ChessPiece> piece : pieces.entrySet()) {
            if (piece.getValue().getTeamColor() == color) {
                if (piece.getValue().getPieceType() == type) return piece.getKey();
            }
        }
        return null;
    }

    /**
     * Sets the board to the default starting board
     * (How the game of chess normally starts)
     */
    public void resetBoard() {
        pieces.clear();
        pieces.putAll(defaultBoard);
    }

    @Override
    public String toString() {
        StringBuilder s = new StringBuilder();
        s.append("\n");
        for (int i = 1; i <= 8; i++) {
            for (int j = 1; j <= 8; j++) {
                s.append("|");
                ChessPiece thisPiece = pieces.get(new ChessPosition(i,j));
                if (thisPiece != null) {
                    s.append(thisPiece.toString());
                } else {
                    s.append(" ");
                }
            }
            s.append("|\n");
        }
        return s.toString();
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
        ChessBoard newBoard = (ChessBoard) obj;
        return this.hashCode() ==  newBoard.hashCode();
    }

    public HashMap<ChessPosition, ChessPiece> getPieces() {
        return pieces;
    }
}
