package chess;
import jdk.jshell.spi.ExecutionControl;

import java.util.HashMap;
/**
 * A chessboard that can hold and rearrange chess pieces.
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessBoard {

    public ChessBoard() {
        
    }
    private HashMap<ChessPosition, ChessPiece> pieces = new HashMap<>();

    /**
     * Adds a chess piece to the chessboard
     *
     * @param position where to add the piece to
     * @param piece    the piece to add
     */
    public void addPiece(ChessPosition position, ChessPiece piece) {
        pieces.put(position, piece);
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
     * Sets the board to the default starting board
     * (How the game of chess normally starts)
     */
    public void resetBoard() {
        for (int i = 1; i < 9; i++) {
            for (int j = 1; j < 9; j++) {
                if (i == 1) {
                    switch (j) {
                        case 1:
                        case 8:
                            pieces.put(new ChessPosition(i,j), new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.ROOK));
                            break;
                        case 2:
                        case 7:
                            pieces.put(new ChessPosition(i,j), new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.KNIGHT));
                            break;
                        case 3:
                        case 6:
                            pieces.put(new ChessPosition(i,j), new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.BISHOP));
                            break;
                        case 4:
                            pieces.put(new ChessPosition(i,j), new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.QUEEN));
                            break;
                        case 5:
                            pieces.put(new ChessPosition(i,j), new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.KING));
                            break;
                    }
                } else if (i == 2) {
                    pieces.put(new ChessPosition(i,j), new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.PAWN));
                } else if (i == 7) {
                    pieces.put(new ChessPosition(i,j), new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.PAWN));
                } else if (i == 8) {
                    switch (j) {
                        case 1:
                        case 8:
                            pieces.put(new ChessPosition(i,j), new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.ROOK));
                            break;
                        case 2:
                        case 7:
                            pieces.put(new ChessPosition(i,j), new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.KNIGHT));
                            break;
                        case 3:
                        case 6:
                            pieces.put(new ChessPosition(i,j), new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.BISHOP));
                            break;
                        case 4:
                            pieces.put(new ChessPosition(i,j), new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.QUEEN));
                            break;
                        case 5:
                            pieces.put(new ChessPosition(i,j), new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.KING));
                            break;
                    }
                }
            }
        }
        //throw new RuntimeException("Not implemented");
    }
}
