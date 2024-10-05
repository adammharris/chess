package chess;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

public class KingMoveCalculator implements PieceMoveCalculator {
    @Override
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        HashSet<ChessMove> moves = new HashSet<>();
        addMoveIfValid(board, moves, myPosition, 0, -1);
        addMoveIfValid(board, moves, myPosition, 0, 1);
        addMoveIfValid(board, moves, myPosition, -1, 0);
        addMoveIfValid(board, moves, myPosition, -1, -1);
        addMoveIfValid(board, moves, myPosition, -1, 1);
        addMoveIfValid(board, moves, myPosition, 1, 0);
        addMoveIfValid(board, moves, myPosition, 1, -1);
        addMoveIfValid(board, moves, myPosition, 1, 1);

        addCastlingMoves(board, moves, myPosition, board.getPiece(myPosition).getTeamColor());
        return moves;
    }
    //public Collection<ChessMove> teamPieceMoves(ChessBoard board, ChessPosition myPosition) {}

    private void addMoveIfValid(ChessBoard board, HashSet<ChessMove> moves, ChessPosition position, int addRow, int addCol) {
        int newRow = position.getRow() + addRow;
        int newCol = position.getColumn() + addCol;
        if (newRow > 8 || newRow < 1 || newCol > 8 || newCol < 1) return;
        ChessPosition newPos = new ChessPosition(newRow, newCol);
        ChessPiece atPos = board.getPiece(newPos);
        if (atPos != null) {
            if (atPos.getTeamColor() != board.getPiece(position).getTeamColor()) {
                moves.add(new ChessMove(position, newPos));
            }
        } else {
            moves.add(new ChessMove(position, newPos));
        }
    }


    private void addCastlingMoves(ChessBoard board, HashSet<ChessMove> moves, ChessPosition kingPosition, ChessGame.TeamColor teamColor) {
        if (canCastleKingside(board, kingPosition, teamColor)) {
            moves.add(new ChessMove(kingPosition, new ChessPosition(kingPosition.getRow(), kingPosition.getColumn() + 2)));
        }
        if (canCastleQueenside(board, kingPosition, teamColor)) {
            moves.add(new ChessMove(kingPosition, new ChessPosition(kingPosition.getRow(), kingPosition.getColumn() - 2)));
        }
    }

    private boolean canCastleKingside(ChessBoard board, ChessPosition kingPosition, ChessGame.TeamColor teamColor) {
        /*
        1. Neither the King nor Rook have moved since the game started
        2. There are no pieces between the King and the Rook (spacesEmpty)
        3. The King is not in Check (tested in validMoves)
        4. Both your Rook and King will be safe after making the move (cannot be captured by any enemy pieces).
         */
        if (board.getPiece(kingPosition).hasMoved) return false;
        ChessPiece rook = board.getPiece(new ChessPosition(kingPosition.getRow(), 8));
        if (rook != null && rook.hasMoved) return false;

        ChessPiece bishop = board.getPiece(new ChessPosition(kingPosition.getRow(), 6));
        ChessPiece knight = board.getPiece(new ChessPosition(kingPosition.getRow(), 7));
        if (bishop != null || knight != null) return false;

        if (isInDanger(board, kingPosition, teamColor)) return false;

        if (!(kingPosition.getRow() == 8 || kingPosition.getRow() == 1)) return false;
        ChessBoard simulate = new ChessBoard(board);
        simulate.movePiece(kingPosition, new ChessPosition(kingPosition.getRow(), 7));
        simulate.movePiece(new ChessPosition(kingPosition.getRow(), 8), new ChessPosition(kingPosition.getRow(), 6));
        if (isInDanger(simulate, new ChessPosition(kingPosition.getRow(), 7), teamColor)) return false;
        return !isInDanger(simulate, new ChessPosition(kingPosition.getRow(), 6), teamColor);
    }

    private boolean canCastleQueenside(ChessBoard board, ChessPosition kingPosition, ChessGame.TeamColor teamColor) {
        // Implement the logic to check if queenside castling is possible
        // Ensure the king and rook have not moved, no pieces between them, and no squares are under attack
        if (board.getPiece(kingPosition).hasMoved) return false;
        ChessPiece rook = board.getPiece(new ChessPosition(kingPosition.getRow(), 1));
        if (rook != null && rook.hasMoved) return false;

        ChessPiece queen = board.getPiece(new ChessPosition(kingPosition.getRow(), 4));
        ChessPiece bishop = board.getPiece(new ChessPosition(kingPosition.getRow(), 3));
        ChessPiece knight = board.getPiece(new ChessPosition(kingPosition.getRow(), 2));
        if (queen != null || bishop != null || knight != null) return false;

        return !isInDanger(board, kingPosition, teamColor);
    }

    private boolean isInDanger(ChessBoard board, ChessPosition position, ChessGame.TeamColor teamColor) {
        HashMap<ChessPosition, ChessPiece> pieces = board.getPieces();
        for (Map.Entry<ChessPosition, ChessPiece> entry : pieces.entrySet()) {
            ChessPiece piece = entry.getValue();
            if (piece == null) {
                System.out.println(position);
            }
            if (piece == null) return false;
            if (piece.getTeamColor() != teamColor && piece.getPieceType() != ChessPiece.PieceType.KING) {
                Collection<ChessMove> moves = piece.teamPieceMoves(board, entry.getKey(), piece.getTeamColor());
                for (ChessMove move : moves) {
                    if (move.getEndPosition().equals(position)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }
}


