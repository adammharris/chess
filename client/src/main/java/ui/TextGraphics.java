package ui;

import chess.ChessBoard;
import chess.ChessGame;
import chess.ChessPiece;
import chess.ChessPosition;

import static ui.EscapeSequences.*;

public class TextGraphics {
    private static final String BORDER_COLOR = SET_BG_COLOR_DARK_GREEN;
    private static final String BORDER_TEXT_COLOR = SET_TEXT_COLOR_BLACK;
    private static final String VERTICAL_BORDER = "ABCDEFGH";
    private static final String HORIZONTAL_BORDER = "12345678";

    private static String getPieceChar(ChessGame.TeamColor color, ChessPiece.PieceType type) {
        if (color == ChessGame.TeamColor.WHITE) {
            return getPieceChar(type, WHITE_PAWN, WHITE_ROOK, WHITE_KNIGHT, WHITE_BISHOP, WHITE_QUEEN, WHITE_KING);
        } else {
            return getPieceChar(type, BLACK_PAWN, BLACK_ROOK, BLACK_KNIGHT, BLACK_BISHOP, BLACK_QUEEN, BLACK_KING);
        }
    }

    private static String getPieceChar(ChessPiece.PieceType type, String pawn, String rook, String knight, String bishop, String queen, String king) {
        return switch (type) {
            case PAWN -> pawn;
            case ROOK -> rook;
            case KNIGHT -> knight;
            case BISHOP -> bishop;
            case QUEEN -> queen;
            case KING -> king;
        };
    }

    private static String getCharAtPosition(ChessBoard board, int row, int col) {
        if (row == 0 || row == 9) {
            if (col > 0 && col < 9) {
                return VERTICAL_BORDER.substring(col - 1, col - 1);
            } else {
                return " ";
            }
        } else if (col == 0 || col == 9) {
            return HORIZONTAL_BORDER.substring(row - 1, row - 1);
        } else {
            ChessPiece thisPiece = board.getPiece(new ChessPosition(row, col));
            if (thisPiece == null) {
                return " ";
            } else {
                return getPieceChar(thisPiece.getTeamColor(), thisPiece.getPieceType());
            }
        }
    }

    public static String constructBoard(ChessBoard board) {
        StringBuilder sb = new StringBuilder();

        sb.append(BORDER_COLOR);
        sb.append(BORDER_TEXT_COLOR);

        for (int row = 0; row <= 9; row++) {
            for (int col = 0; col <= 9; col++) {
                sb.append(getCharAtPosition(board, row, col));
            }
        }
        return sb.toString();
    }
}
