package chess;

import java.util.Collection;
import java.util.HashSet;

public class PawnMoveCalculator implements PieceMoveCalculator {
    @Override
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        HashSet<ChessMove> moves = new HashSet<>();
        ChessPiece thisPiece = board.getPiece(myPosition);
        if (thisPiece.getTeamColor() == ChessGame.TeamColor.WHITE) {
            boolean canAdvance = addMoveIfValid(board, moves, myPosition, 1); // regular advance
            if (myPosition.getRow() == 2 && canAdvance) {
                addMoveIfValid(board, moves, myPosition, 2); // first double advance
            }
            addIfAttackable(board, moves, myPosition, 1, 1);
            addIfAttackable(board, moves, myPosition, 1, -1);
            addEnPassantMoves(board, moves, myPosition, 1);
        } else {
            boolean canAdvance = addMoveIfValid(board, moves, myPosition, -1);
            if (myPosition.getRow() == 7 && canAdvance) {
                addMoveIfValid(board, moves, myPosition, -2);
            }
            addIfAttackable(board, moves, myPosition, -1, 1);
            addIfAttackable(board, moves, myPosition, -1, -1);
            addEnPassantMoves(board, moves, myPosition, -1);
        }
        return moves;
    }
    private boolean addMoveIfValid(ChessBoard board, HashSet<ChessMove> moves, ChessPosition position, int addRow) {
        int newRow = position.getRow() + addRow;
        int newCol = position.getCol();
        if (newRow > 8
                || newRow < 1
                || newCol > 8
                || newCol < 1) {
            return false;
        }
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
        int newCol = position.getCol() + addCol;
        if (newRow > 8
                || newRow < 1
                || newCol > 8
                || newCol < 1) {
            return;
        }
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

    private void addEnPassantMoves(ChessBoard board, HashSet<ChessMove> moves, ChessPosition position, int direction) {
        ChessMove lastMove = board.getLastMove();
        if (lastMove == null) {
            return;
        }

        ChessPosition lastStart = lastMove.getStartPosition();
        ChessPosition lastEnd = lastMove.getEndPosition();
        ChessPiece lastPiece = board.getPiece(lastEnd);

        boolean victimIsPawn = lastPiece != null && lastPiece.getPieceType() == ChessPiece.PieceType.PAWN;
        boolean pawnMovedTwoSpaces = Math.abs(lastEnd.getRow() - lastStart.getRow()) == 2;
        boolean pawnIsOneColumnAway = Math.abs(lastEnd.getCol() - position.getCol()) == 1;
        boolean onSameRow = lastEnd.getRow() == position.getRow();
        if (victimIsPawn && pawnMovedTwoSpaces && pawnIsOneColumnAway && onSameRow) {
            ChessPosition enPassantCapture = new ChessPosition(position.getRow() + direction, lastEnd.getCol());
            moves.add(new ChessMove(position, enPassantCapture));
        }
    }

}
