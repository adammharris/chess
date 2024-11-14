package ui;

import chess.ChessBoard;
import chess.ChessGame;
import chess.ChessPiece;
import chess.ChessPosition;

import static ui.EscapeSequences.*;

public class TextGraphics {
    private static final String BORDER_COLOR = SET_BG_COLOR_DARK_GREEN;
    private static final String BORDER_TEXT_COLOR = SET_TEXT_COLOR_WHITE;
    private static final String VERTICAL_BORDER = "ABCDEFGH";
    private static final String HORIZONTAL_BORDER = "12345678";
    private static final String WHITE_SQUARE = SET_BG_COLOR_LIGHT_GREY;
    private static final String BLACK_SQUARE = SET_BG_COLOR_DARK_GREY;

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
        StringBuilder sb = new StringBuilder();
        if (row == 0 || row == 9) {
            if (col == 0) {
                sb.append(BORDER_COLOR).append(BORDER_TEXT_COLOR);
            }
            if (col > 0 && col < 9) {
                sb.append(" ").append(VERTICAL_BORDER.charAt(col - 1)).append(" ");
            } else {
                sb.append(EMPTY);
            }
            if (col == 9) {
                sb.append(RESET_BG_COLOR).append(RESET_TEXT_COLOR);
            }
        } else if (col == 0 || col == 9) {
            sb.append(BORDER_TEXT_COLOR).append(BORDER_COLOR);
            sb.append(" ").append(HORIZONTAL_BORDER.charAt(row - 1)).append(" ");
            sb.append(RESET_TEXT_COLOR).append(RESET_BG_COLOR);
        } else {
            if ((row + col) % 2 == 0) {
                sb.append(BLACK_SQUARE);
            } else {
                sb.append(WHITE_SQUARE);
            }
            ChessPiece thisPiece = board.getPiece(new ChessPosition(row, col));
            if (thisPiece == null) {
                sb.append(EMPTY);
            } else {
                if (thisPiece.getTeamColor() == ChessGame.TeamColor.WHITE) {
                    sb.append(SET_TEXT_COLOR_WHITE);
                } else {
                    sb.append(SET_TEXT_COLOR_BLACK);
                }
                sb.append(getPieceChar(thisPiece.getTeamColor(), thisPiece.getPieceType()));
            }
        }
        return sb.toString();
    }

    public static String constructBoard(ChessBoard board) {
        StringBuilder sb = new StringBuilder();

        sb.append(RESET_BG_COLOR);
        sb.append(RESET_TEXT_COLOR);
        sb.append('\n');

        for (int row = 0; row <= 9; row++) {
            for (int col = 0; col <= 9; col++) {
                sb.append(getCharAtPosition(board, row, col));
            }
            sb.append(EMPTY);
            sb.append('\n');
            sb.append(RESET_BG_COLOR);
            sb.append(RESET_TEXT_COLOR);
        }
        return sb.toString();
    }
}
