package ui;

import chess.ChessBoard;
import chess.ChessGame;
import chess.ChessPiece;
import chess.ChessPosition;

import static ui.EscapeSequences.*;

public class TextGraphics {
    private static final String BORDER_COLOR = SET_BG_COLOR_BLACK;
    private static final String BORDER_TEXT_COLOR = SET_TEXT_COLOR_WHITE;
    private static final String VERTICAL_BORDER = "ABCDEFGH";
    private static final String HORIZONTAL_BORDER = "12345678";
    private static final String WHITE_SQUARE = SET_BG_COLOR_WHITE;
    private static final String BLACK_SQUARE = SET_BG_COLOR_LIGHT_GREY;
    private static final String BLACK_SQUARE_HIGHLIGHT = SET_BG_COLOR_DARK_GREEN;
    private static final String WHITE_SQUARE_HIGHLIGHT = SET_BG_COLOR_GREEN;
    private static final String WHITE_PIECE_COLOR = SET_TEXT_COLOR_DARK_GREY;
    private static final String BLACK_PIECE_COLOR = SET_TEXT_COLOR_BLACK;

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

    private static String getCharAtPosition(ChessBoard board, int row, int col, boolean orientedToWhite, ChessPosition[] highlights) {
        StringBuilder sb = new StringBuilder();
        String verticalBorder;
        String horizontalBorder;
        boolean isHighlighted = false;
        if (row >= 1 && row <= 8 && col >= 1 && col <= 8) {
            ChessPosition currentPosition;
            if (orientedToWhite) {
                currentPosition = new ChessPosition(9 - row, col);
            } else {
                currentPosition = new ChessPosition(row, 9 - col);
            }
            for (ChessPosition highlight : highlights) {
                if (currentPosition.equals(highlight)) {
                    isHighlighted = true;
                    break;
                }
            }
        }
        if (orientedToWhite) {
            verticalBorder = VERTICAL_BORDER;
            sb.append(HORIZONTAL_BORDER);
            sb.reverse();
            horizontalBorder = sb.toString();
            sb.setLength(0);
        } else {
            sb.append(VERTICAL_BORDER);
            sb.reverse();
            verticalBorder = sb.toString();
            sb.setLength(0);
            horizontalBorder = HORIZONTAL_BORDER;
        }

        if (row == 0 || row == 9) {
            if (col == 0) {
                sb.append(BORDER_COLOR).append(BORDER_TEXT_COLOR).append(SET_TEXT_BOLD);
            }
            if (col > 0 && col < 9) {
                sb.append(" ").append(verticalBorder.charAt(col - 1)).append(" ");
            } else {
                sb.append(EMPTY);
            }
            if (col == 9) {
                sb.append(RESET_BG_COLOR).append(RESET_TEXT_COLOR).append(RESET_TEXT_BOLD_FAINT);
            }
        } else if (col == 0 || col == 9) {
            sb.append(BORDER_TEXT_COLOR).append(BORDER_COLOR);
            sb.append(" ").append(horizontalBorder.charAt(row - 1)).append(" ");
            sb.append(RESET_TEXT_COLOR).append(RESET_BG_COLOR);
        } else {
            if ((row + col) % 2 == 1) {
                if (!isHighlighted) {
                    sb.append(BLACK_SQUARE);
                } else {
                    sb.append(BLACK_SQUARE_HIGHLIGHT);
                }
            } else {
                if (!isHighlighted) {
                    sb.append(WHITE_SQUARE);
                } else {
                    sb.append(WHITE_SQUARE_HIGHLIGHT);
                }
            }
            ChessPiece thisPiece;
            col = 9 - col;
            if (orientedToWhite) {
                thisPiece = board.getPiece(new ChessPosition(9 - row, 9 - col));
            } else {
                thisPiece = board.getPiece(new ChessPosition(row, col));
            }

            if (thisPiece == null) {
                sb.append(EMPTY);
            } else {
                if (thisPiece.getTeamColor() == ChessGame.TeamColor.WHITE) {
                    sb.append(WHITE_PIECE_COLOR);
                } else {
                    sb.append(BLACK_PIECE_COLOR);
                }
                sb.append(getPieceChar(thisPiece.getTeamColor(), thisPiece.getPieceType()));
            }
        }
        return sb.toString();
    }

    public static String constructBoard(ChessBoard board, boolean orientedToWhite, ChessPosition[] highlights) {
        StringBuilder sb = new StringBuilder();

        sb.append(RESET_BG_COLOR);
        sb.append(RESET_TEXT_COLOR);
        sb.append('\n');

        for (int row = 0; row <= 9; row++) {
            for (int col = 0; col <= 9; col++) {
                sb.append(getCharAtPosition(board, row, col, orientedToWhite, highlights));
            }
            sb.append(EMPTY);
            sb.append('\n');
            sb.append(RESET_BG_COLOR);
            sb.append(RESET_TEXT_COLOR);
        }
        return sb.toString();
    }
}
