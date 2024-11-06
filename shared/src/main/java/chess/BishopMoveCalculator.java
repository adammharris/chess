package chess;

import java.util.Collection;
import java.util.HashSet;

public class BishopMoveCalculator implements PieceMoveCalculator {

    public enum Direction {
        UP_RIGHT,
        UP_LEFT,
        DOWN_RIGHT,
        DOWN_LEFT
    }

    @Override
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        HashSet<ChessMove> moves = new HashSet<>();
        calculateRow(board, myPosition, moves, Direction.UP_RIGHT);
        calculateRow(board, myPosition, moves, Direction.UP_LEFT);
        calculateRow(board, myPosition, moves, Direction.DOWN_RIGHT);
        calculateRow(board, myPosition, moves, Direction.DOWN_LEFT);
        return moves;
    }
    private void calculateRow(ChessBoard board, ChessPosition position, HashSet<ChessMove> moves, Direction dir) {
        int j = position.getCol();
        switch (dir) {
            case UP_RIGHT:
                j++;
                for (int i = position.getRow() + 1; i <= 8 && j <=8; i++) {
                    ChessPosition newPos = new ChessPosition(i, j);
                    boolean foundPiece = addMoveIfValid(board, moves, position, newPos);
                    j++;
                    if (foundPiece) {
                        break;
                    }
                }
                break;
            case UP_LEFT:
                j--;
                for (int i = position.getRow() + 1; i <= 8 && j >= 1; i++) {
                    ChessPosition newPos = new ChessPosition(i, j);
                    boolean foundPiece = addMoveIfValid(board, moves, position, newPos);
                    j--;
                    if (foundPiece) {
                        break;
                    }
                }
                break;
            case DOWN_RIGHT:
                j++;
                for (int i = position.getRow() - 1; i >= 1 && j <= 8; i--) {
                    ChessPosition newPos = new ChessPosition(i, j);
                    boolean foundPiece = addMoveIfValid(board, moves, position, newPos);
                    j++;
                    if (foundPiece) {
                        break;
                    }
                }
                break;
            case DOWN_LEFT:
                j--;
                for (int i = position.getRow() - 1; i >= 1 && j >= 1; i--) {
                    ChessPosition newPos = new ChessPosition(i, j);
                    boolean foundPiece = addMoveIfValid(board, moves, position, newPos);
                    j--;
                    if (foundPiece) {
                        break;
                    }
                }
                break;
        }

    }
    protected boolean addMoveIfValid(ChessBoard board, HashSet<ChessMove> moves, ChessPosition position, ChessPosition newPos) {
        ChessPiece atPos = board.getPiece(newPos);
        if (atPos != null) {
            if (atPos.getTeamColor() != board.getPiece(position).getTeamColor()) {
                moves.add(new ChessMove(position, newPos));
            }
            return true;
        } else {
            moves.add(new ChessMove(position, newPos));
        }
        return false;
    }
}

