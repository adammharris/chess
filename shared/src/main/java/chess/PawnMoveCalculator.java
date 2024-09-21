package chess;

import java.util.Collection;
import java.util.HashSet;

public class PawnMoveCalculator extends PieceMovesCalculator {
    @Override
    Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition position) {
        HashSet<ChessMove> moves = new HashSet<>();
        addAttack(board, position, moves);
        addAdvance(board, position, moves);
        return moves;
    }

    private void addAttack(ChessBoard board, ChessPosition position, HashSet<ChessMove> moves) {
        ChessGame.TeamColor thisColor = board.getPiece(position).getTeamColor();
        int row = position.getRow();
        int col = position.getColumn();
        ChessPosition attack1 = null;
        ChessPosition attack2 = null;
        if (thisColor == ChessGame.TeamColor.WHITE) {
            row++; col--;
            if (row > 8 || row < 1 || col > 8 || col < 1) {
                attack1 = new ChessPosition(row, col);
            }
            col += 2;
            if (row > 8 || row < 1 || col > 8 || col < 1) {
                attack2 = new ChessPosition(row, col);
            }
        } else {
            row--; col--;
            if (row > 8 || row < 1 || col > 8 || col < 1) {
                attack1 = new ChessPosition(row, col);
            }
            col += 2;
            if (row > 8 || row < 1 || col > 8 || col < 1) {
                attack2 = new ChessPosition(row, col);
            }
        }
        if (attack1 != null) {
            ChessPiece potentialAttack = board.getPiece(attack1);
            if (potentialAttack != null) {
                if (thisColor != potentialAttack.getTeamColor()) {
                    moves.add(new ChessMove(position, new ChessPosition(row, col)));
                }
            }
        }
        if (attack2 != null) {
            ChessPiece potentialAttack = board.getPiece(attack2);
            if (potentialAttack != null) {
                if (thisColor != potentialAttack.getTeamColor()) {
                    moves.add(new ChessMove(position, new ChessPosition(row, col)));
                }
            }
        }
    }
    private boolean addAdvance(ChessBoard board, ChessPosition position, HashSet<ChessMove> moves) {
        int row = position.getRow();
        ChessGame.TeamColor thisColor = board.getPiece(position).getTeamColor();
        if (thisColor == ChessGame.TeamColor.WHITE) {
            row++;
            if (row < 8) return false;
            ChessPosition newPosition = new ChessPosition(row, position.getColumn());
            if (board.getPiece(newPosition) != null) return false;
            moves.add(new ChessMove(position, newPosition));
            return true;
        } else {
            row--;
            if (row < 2) return false;
            ChessPosition newPosition = new ChessPosition(row, position.getColumn());
            if (board.getPiece(newPosition) != null) return false;
            moves.add(new ChessMove(position, newPosition));
            return true;
        }
    }
}
