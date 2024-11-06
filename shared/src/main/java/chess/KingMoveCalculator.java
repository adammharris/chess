package chess;

import java.util.Collection;
import java.util.HashSet;

public class KingMoveCalculator implements PieceMoveCalculator {
    @Override
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        HashSet<ChessMove> moves = new HashSet<>();
        KnightMoveCalculator kmc = new KnightMoveCalculator();
        kmc.addMoveIfValid(board, moves, myPosition, 0, -1);
        kmc.addMoveIfValid(board, moves, myPosition, 0, 1);
        kmc.addMoveIfValid(board, moves, myPosition, -1, 0);
        kmc.addMoveIfValid(board, moves, myPosition, -1, -1);
        kmc.addMoveIfValid(board, moves, myPosition, -1, 1);
        kmc.addMoveIfValid(board, moves, myPosition, 1, 0);
        kmc.addMoveIfValid(board, moves, myPosition, 1, -1);
        kmc.addMoveIfValid(board, moves, myPosition, 1, 1);

        addCastlingMoves(board, moves, myPosition, board.getPiece(myPosition).getTeamColor());
        return moves;
    }

    private void addCastlingMoves(ChessBoard board, HashSet<ChessMove> moves, ChessPosition kingPosition, ChessGame.TeamColor teamColor) {
        if (canCastleKingside(board, kingPosition, teamColor)) {
            moves.add(new ChessMove(new ChessPosition(kingPosition.getRow(), kingPosition.getCol()), new ChessPosition(kingPosition.getRow(), kingPosition.getCol() + 2)));
        }
        if (canCastleQueenside(board, kingPosition, teamColor)) {
            moves.add(new ChessMove(new ChessPosition(kingPosition.getRow(), kingPosition.getCol()), new ChessPosition(kingPosition.getRow(), kingPosition.getCol() - 2)));
        }
    }

    private boolean canCastleKingside(ChessBoard board, ChessPosition kingPosition, ChessGame.TeamColor teamColor) {
        /*
        1. Neither the King nor Rook have moved since the game started
        2. There are no pieces between the King and the Rook (spacesEmpty)
        3. The King is not in Check (tested in validMoves)
        4. Both your Rook and King will be safe after making the move (cannot be captured by any enemy pieces).
         */
        int currentRow = kingPosition.getRow();
        if (!(kingPosition.getRow() == 8 || kingPosition.getRow() == 1)) {
            return false;
        }
        if (!(kingPosition.getCol() == 5)) {
            return false;
        }
        if (board.getPiece(kingPosition).hasMoved) {
            return false;
        }
        ChessPosition rookPosition = new ChessPosition(currentRow, 8);
        if (board.getPiece(rookPosition) == null) {
            return false;
        }
        if (board.getPiece(rookPosition).hasMoved) {
            return false;
        }
        if (board.getPiece(rookPosition).getPieceType() != ChessPiece.PieceType.ROOK) {
            return false;
        }

        ChessPiece bishop = board.getPiece(new ChessPosition(kingPosition.getRow(), 6));
        ChessPiece knight = board.getPiece(new ChessPosition(kingPosition.getRow(), 7));
        if (bishop != null || knight != null) {
            return false;
        }

        if (isInDanger(board, kingPosition, teamColor)) {
            return false;
        }

        ChessBoard simulate = new ChessBoard(board);
        simulate.movePiece(new ChessPosition(kingPosition.getRow(), kingPosition.getCol()), new ChessPosition(kingPosition.getRow(), 7));
        simulate.movePiece(new ChessPosition(kingPosition.getRow(), 8), new ChessPosition(kingPosition.getRow(), 6));
        if (isInDanger(simulate, new ChessPosition(kingPosition.getRow(), 7), teamColor)) {
            return false;
        }
        return !isInDanger(simulate, new ChessPosition(kingPosition.getRow(), 6), teamColor);
    }

    private boolean canCastleQueenside(ChessBoard board, ChessPosition kingPosition, ChessGame.TeamColor teamColor) {
        int currentRow = kingPosition.getRow();
        if (!(kingPosition.getRow() == 8
                || kingPosition.getRow() == 1)
                || board.getPiece(kingPosition).hasMoved) {
            return false;
        }
        if (board.getPiece(kingPosition).hasMoved) {
            return false;
        }
        ChessPosition rookPosition = new ChessPosition(currentRow, 1);
        if (board.getPiece(rookPosition) == null) {
            return false;
        }
        if (board.getPiece(rookPosition).hasMoved) {
            return false;
        }
        if (board.getPiece(rookPosition).getPieceType() != ChessPiece.PieceType.ROOK) {
            return false;
        }

        ChessPosition queenPosition = new ChessPosition(kingPosition.getRow(), 4);
        ChessPosition bishopPosition = new ChessPosition(kingPosition.getRow(), 3);
        ChessPosition knightPosition = new ChessPosition(kingPosition.getRow(), 2);
        if (board.getPiece(bishopPosition) != null
                || board.getPiece(knightPosition) != null
                || board.getPiece(queenPosition) != null) {
            return false;
        }

        if (isInDanger(board, kingPosition, teamColor)) {
            return false;
        }


        ChessBoard simulate = new ChessBoard(board);
        simulate.movePiece(kingPosition, knightPosition);
        simulate.movePiece(rookPosition, bishopPosition);
        if (isInDanger(simulate, knightPosition, teamColor)) {
            return false;
        }
        return !isInDanger(simulate, bishopPosition, teamColor);
    }

    private boolean isInDanger(ChessBoard board, ChessPosition position, ChessGame.TeamColor teamColor) {

        ChessPiece[] pieces = board.getPieces();
        for (ChessPiece piece : pieces) {
            if (piece.getTeamColor() != teamColor) {
                Collection<ChessMove> moves = piece.pieceMoves(board, piece.position);
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


