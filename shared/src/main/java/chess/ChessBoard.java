package chess;
import java.util.ArrayList;

/**
 * A chessboard that can hold and rearrange chess pieces.
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessBoard {
    private final ArrayList<ChessPiece> pieces = new ArrayList<>();
    private final static ChessBoard DEFAULT_BOARD = new ChessBoard();
    private ChessMove lastMove;

    static {
        defineDefault();
    }

    public ChessBoard() {}

    // Used to simulate moves
    public ChessBoard(ChessBoard board) {
        for (ChessPiece piece : board.getPieces()) {
            this.pieces.add(new ChessPiece(piece.getTeamColor(), piece.getPieceType(), new ChessPosition(piece.position.getRow(), piece.position.getCol())));
        }
        this.lastMove = board.getLastMove();
    }

    private static void defineDefault() {
        DEFAULT_BOARD.addPiece(new ChessPosition(1, 1), new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.ROOK));
        DEFAULT_BOARD.addPiece(new ChessPosition(1, 2), new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.KNIGHT));
        DEFAULT_BOARD.addPiece(new ChessPosition(1, 3), new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.BISHOP));
        DEFAULT_BOARD.addPiece(new ChessPosition(1, 4), new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.QUEEN));
        DEFAULT_BOARD.addPiece(new ChessPosition(1, 5), new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.KING));
        DEFAULT_BOARD.addPiece(new ChessPosition(1, 6), new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.BISHOP));
        DEFAULT_BOARD.addPiece(new ChessPosition(1, 7), new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.KNIGHT));
        DEFAULT_BOARD.addPiece(new ChessPosition(1, 8), new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.ROOK));
        for (int i = 1; i <= 8; i++) {
            DEFAULT_BOARD.addPiece(new ChessPosition(2, i), new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.PAWN));
        }

        for (int i = 1; i <= 8; i++) {
            DEFAULT_BOARD.addPiece(new ChessPosition(7, i), new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.PAWN));
        }
        DEFAULT_BOARD.addPiece(new ChessPosition(8, 1), new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.ROOK));
        DEFAULT_BOARD.addPiece(new ChessPosition(8, 2), new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.KNIGHT));
        DEFAULT_BOARD.addPiece(new ChessPosition(8, 3), new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.BISHOP));
        DEFAULT_BOARD.addPiece(new ChessPosition(8, 4), new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.QUEEN));
        DEFAULT_BOARD.addPiece(new ChessPosition(8, 5), new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.KING));
        DEFAULT_BOARD.addPiece(new ChessPosition(8, 6), new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.BISHOP));
        DEFAULT_BOARD.addPiece(new ChessPosition(8, 7), new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.KNIGHT));
        DEFAULT_BOARD.addPiece(new ChessPosition(8, 8), new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.ROOK));
    }

    /**
     * Adds a chess piece to the chessboard
     *
     * @param position where to add the piece to
     * @param piece    the piece to add
     */
    public void addPiece(ChessPosition position, ChessPiece piece) {
        piece.position = position;
        pieces.add(piece);
    }

    /**
     * Gets a chess piece on the chessboard
     *
     * @param position The position to get the piece from
     * @return Either the piece at the position, or null if no piece is at that
     * position
     */
    public ChessPiece getPiece(ChessPosition position) {
        for (ChessPiece piece : pieces) {
            if (piece.position.equals(position)) {
                return piece;
            }
        }
        return null;
    }

    /**
     * Removes a chess piece from the chess board
     *
     * @param position position of piece to be removed
     * @return ChessPiece piece that is removed, as returned by HashMap<>.remove()
     */
    public ChessPiece removePiece(ChessPosition position) {
        for (int i = 0; i < pieces.size(); i++) {
            if (pieces.get(i).position.equals(position)) {
                pieces.get(i).position = null;
                return pieces.remove(i);
            }
        }
        return null;
    }
    /**
     * Moves a chess piece from one position to another
     *
     * @param oldPos Original position of chess piece to be moved. Uses getPiece
     * @param newPos New position of chess piece to be moved. Uses removePiece and addPiece
     */
    public void movePiece(ChessPosition oldPos, ChessPosition newPos) {
        if (getPiece(newPos) != null) {
            removePiece(newPos);
        }
        addPiece(newPos, removePiece(oldPos));
    }

    public void executeMove(ChessMove move) {
        ChessPiece myPiece;
        movePiece(move.getStartPosition(), move.getEndPosition());
        myPiece = getPiece(move.getEndPosition());
        if (myPiece != null
                && myPiece.getPieceType() == ChessPiece.PieceType.PAWN) {
            // En Passant capture
            ChessMove lastMove = getLastMove();
            if (lastMove != null) {
                ChessPosition lastEnd = lastMove.getEndPosition();
                boolean isEnPassantCapture = Math.abs(move.getStartPosition().getCol()
                        - move.getEndPosition().getCol()) == 1 &&
                        Math.abs(move.getStartPosition().getRow()
                                - move.getEndPosition().getRow()) == 1;
                if (isEnPassantCapture) {
                    ChessPosition capturedPawnPosition = new ChessPosition(lastEnd.getRow(), move.getEndPosition().getCol());
                    removePiece(capturedPawnPosition);
                }
            }
        }

        if (myPiece != null && myPiece.getPieceType() == ChessPiece.PieceType.KING) {
            // Handle castling
            if (Math.abs(move.getEndPosition().getCol() - move.getStartPosition().getCol()) == 2) {
                // Kingside castling
                if (move.getEndPosition().getCol() > move.getStartPosition().getCol()) {
                    movePiece(new ChessPosition(move.getStartPosition().getRow(), 8), new ChessPosition(move.getStartPosition().getRow(), 6));
                }
                // Queenside castling
                else {
                    movePiece(new ChessPosition(move.getStartPosition().getRow(), 1), new ChessPosition(move.getStartPosition().getRow(), 4));
                }
            }
        }
        if ((move.promotion != null)) {
            assert myPiece != null;
            if (myPiece.getPieceType() == ChessPiece.PieceType.PAWN) {
                removePiece(move.getEndPosition());
                addPiece(move.getEndPosition(), new ChessPiece(myPiece.getTeamColor(), move.promotion, move.getEndPosition()));
            }
        }
        if (myPiece != null) {
            myPiece.hasMoved = true;
        }
        lastMove = move;
    }

    public ChessMove getLastMove() {
        return lastMove;
    }

    /**
     * Gets chess piece position based on color and type. Used for getting king in checking for check
     * @param color Color of chess piece to get
     * @param type Type of chess piece to get.
     * @return First piece found of color and type given
     */
    public ChessPosition getPosition(ChessGame.TeamColor color, ChessPiece.PieceType type) {
        for (ChessPiece piece : pieces) {
            if (piece.getTeamColor() == color) {
                if (piece.getPieceType() == type) {
                    return piece.position;
                }
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
        for (ChessPiece piece : DEFAULT_BOARD.getPieces()) {
            pieces.add(new ChessPiece(piece.getTeamColor(), piece.getPieceType(), new ChessPosition(piece.position.getRow(), piece.position.getCol())));
        }
    }

    @Override
    public String toString() {
        StringBuilder s = new StringBuilder();
        s.append("\n");
        for (int i = 1; i <= 8; i++) {
            for (int j = 1; j <= 8; j++) {
                s.append("|");
                ChessPiece thisPiece = getPiece(new ChessPosition(i,j));
                if (thisPiece != null) {
                    s.append(thisPiece);
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
        if (obj == this) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (this.getClass() != obj.getClass()) {
            return false;
        }
        ChessBoard newBoard = (ChessBoard) obj;
        return this.hashCode() ==  newBoard.hashCode();
    }

    public ChessPiece[] getPieces() {
        return pieces.toArray(ChessPiece[]::new);
    }
}
