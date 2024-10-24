package chess;

import java.util.Collection;
import java.util.HashSet;

public class RookMoveCalculator implements PieceMoveCalculator {
    @Override
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        HashSet<ChessMove> moves = new HashSet<>();
        calculateRow(board, myPosition, moves, Direction.UP);
        calculateRow(board, myPosition, moves, Direction.DOWN);
        calculateRow(board, myPosition, moves, Direction.LEFT);
        calculateRow(board, myPosition, moves, Direction.RIGHT);
        return moves;
    }
    public enum Direction {
        UP,
        DOWN,
        LEFT,
        RIGHT
    }
    private void calculateRow(ChessBoard board, ChessPosition position, HashSet<ChessMove> moves, Direction dir) {
        BishopMoveCalculator bmc = new BishopMoveCalculator();
        switch (dir) {
            case UP:
                for (int i = position.getRow() + 1; i <= 8; i++) {
                    ChessPosition newPos = new ChessPosition(i, position.getColumn());
                    boolean foundPiece = bmc.addMoveIfValid(board, moves, position, newPos);
                    if (foundPiece) {
                        break;
                    }
                }
                break;
            case DOWN:
                for (int i = position.getRow() - 1; i >= 1; i--) {
                    ChessPosition newPos = new ChessPosition(i, position.getColumn());
                    boolean foundPiece = bmc.addMoveIfValid(board, moves, position, newPos);
                    if (foundPiece) {
                        break;
                    }
                }
                break;
            case LEFT:
                for (int i = position.getColumn() - 1; i >= 1; i--) {
                    ChessPosition newPos = new ChessPosition(position.getRow(), i);

                    boolean foundPiece = bmc.addMoveIfValid(board, moves, position, newPos);
                    if (foundPiece) {
                        break;
                    }
                }
                break;
            case RIGHT:
                for (int i = position.getColumn() + 1; i <= 8; i++) {
                    ChessPosition newPos = new ChessPosition(position.getRow(), i);
                    boolean foundPiece = bmc.addMoveIfValid(board, moves, position, newPos);
                    if (foundPiece) {
                        break;
                    }
                }
                break;
        }
    }
}
