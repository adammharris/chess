package chess;

import java.util.Collection;
import java.util.HashSet;

public class PawnMoveCalculator implements PieceMoveCalculator {
    @Override
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        HashSet<ChessMove> moves = new HashSet<>();
        ChessPiece thisPiece = board.getPiece(myPosition);
        if (thisPiece.getTeamColor() == ChessGame.TeamColor.WHITE) {
            boolean canAdvance = addMoveIfValid(board, moves, myPosition, 1, 0); // regular advance
            if (myPosition.getRow() == 2 && canAdvance) {
                addMoveIfValid(board, moves, myPosition, 2, 0); // first double advance
            }
            addIfAttackable(board, moves, myPosition, 1, 1);
            addIfAttackable(board, moves, myPosition, 1, -1);
        } else {
            boolean canAdvance = addMoveIfValid(board, moves, myPosition, -1, 0);
            if (myPosition.getRow() == 7 && canAdvance) {
                addMoveIfValid(board, moves, myPosition, -2, 0);
            }
            addIfAttackable(board, moves, myPosition, -1, 1);
            addIfAttackable(board, moves, myPosition, -1, -1);
        }
        return moves;
    }
    private boolean addMoveIfValid(ChessBoard board, HashSet<ChessMove> moves, ChessPosition position, int addRow, int addCol) {
        int newRow = position.getRow() + addRow;
        int newCol = position.getColumn() + addCol;
        if (newRow > 8 || newRow < 1 || newCol > 8 || newCol < 1) return false;
        ChessPosition newPos = new ChessPosition(newRow, newCol);
        ChessPiece atPos = board.getPiece(newPos);
        if (atPos == null) {
            addPossiblePromotions(board, moves, position, newPos);
            return true;
        }
        return false;
    }
    private void addIfAttackable(ChessBoard board, HashSet<ChessMove> moves, ChessPosition position, int addRow, int addCol) {
        int newRow = position.getRow() + addRow;
        int newCol = position.getColumn() + addCol;
        if (newRow > 8 || newRow < 1 || newCol > 8 || newCol < 1) return;
        ChessPosition newPos = new ChessPosition(newRow, newCol);
        ChessPiece atPos = board.getPiece(newPos);
        if (atPos != null) {
            if (atPos.getTeamColor() != board.getPiece(position).getTeamColor()) {
                addPossiblePromotions(board, moves, position, newPos);
            }
        }
    }

    private void addPossiblePromotions(ChessBoard board, HashSet<ChessMove> moves, ChessPosition position, ChessPosition newPos) {
        ChessGame.TeamColor myColor = board.getPiece(position).getTeamColor();
        boolean promotable = (
                (myColor == ChessGame.TeamColor.WHITE && newPos.getRow() == 8)
                        || (myColor == ChessGame.TeamColor.BLACK && newPos.getRow() == 1)
        );
        if (promotable) {
            moves.add(new ChessMove(position, newPos, ChessPiece.PieceType.QUEEN));
            moves.add(new ChessMove(position, newPos, ChessPiece.PieceType.BISHOP));
            moves.add(new ChessMove(position, newPos, ChessPiece.PieceType.KNIGHT));
            moves.add(new ChessMove(position, newPos, ChessPiece.PieceType.ROOK));
        } else {
            moves.add(new ChessMove(position, newPos));
        }
    }
}
