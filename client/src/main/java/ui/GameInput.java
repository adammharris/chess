package ui;

import chess.ChessGame;
import chess.ChessPiece;
import chess.ChessPosition;
import model.GameData;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

public class GameInput {
    public static ChessGame.TeamColor getColor(Scanner scanner) throws IOException {
        System.out.print("Please enter player color (`WHITE` or `BLACK`): ");
        String input = scanner.next().toUpperCase();
        return switch (input) {
            case "WHITE" -> ChessGame.TeamColor.WHITE;
            case "BLACK" -> ChessGame.TeamColor.BLACK;
            default -> throw new IOException("Invalid input!");
        };
    }

    public static int getNumber(Scanner scanner, int listSize) throws IOException {
        System.out.printf("Please enter a number between 1 and %s: ", listSize);
        String input = scanner.next();
        try {
            int output = Integer.parseInt(input);
            if (output > listSize) {
                throw new IOException("Number out of range");
            }
            return output;
        } catch (NumberFormatException e) {
            throw new IOException("Input is not a number: " + e);
        }
    }

    public static ChessPosition getChessPosition(Scanner scanner) throws IOException {
        System.out.print("Please enter a chess position as a letter and a number (for example, `A5`): ");
        String input = scanner.next().toUpperCase();
        int col = switch (input.charAt(0)) {
            case 'A' -> 1;
            case 'B' -> 2;
            case 'C' -> 3;
            case 'D' -> 4;
            case 'E' -> 5;
            case 'F' -> 6;
            case 'G' -> 7;
            case 'H' -> 8;
            default -> throw new IOException("Invalid letter/row");
        };
        int row;
        try {
            row = Integer.parseInt(input.substring(1, 2));
            if (row > 8) {
                throw new IOException("Number/row out of range");
            }
        } catch (NumberFormatException e) {
            throw new IOException("Invalid number/row: " + e);
        }
        // Letter is col, Number is row
        // A2 = col 2 row 1
        return new ChessPosition(row, col);
    }

    public static boolean getBoolean(Scanner scanner) throws IOException {
        System.out.print(" Enter `Y` or `N`: ");
        String input = scanner.next().toUpperCase();
        return switch (input) {
            case "Y" -> true;
            case "N" -> false;
            default -> throw new IOException("Invalid input");
        };
    }

    public static ChessPiece.PieceType getPromotion(Scanner scanner) throws IOException {
        String prompt = "What piece would you like to promote your pawn to?";
        String[] list = {"Queen", "Rook", "Bishop", "Knight"};
        String result = getFromList(scanner, prompt, list);
        return switch (result) {
            case "Queen" -> ChessPiece.PieceType.QUEEN;
            case "Rook" -> ChessPiece.PieceType.ROOK;
            case "Bishop" -> ChessPiece.PieceType.BISHOP;
            case "Knight" -> ChessPiece.PieceType.KNIGHT;
            default -> throw new IllegalStateException("Unexpected value: " + result);
        };
    }

    public static String getString(Scanner scanner, String prompt) {
        System.out.print(prompt);
        return scanner.next();
    }

    public static String getGameName(Scanner scanner) {
        String prompt = "Please enter a name for the game: ";
        return getString(scanner, prompt);
    }

    public static String getFromList(Scanner scanner, String prompt, String[] list) throws IOException {
        System.out.println(prompt);
        for (int i = 1; i <= list.length; i++) {
            System.out.printf("%s - %s\n", i, list[i-1]);
        }
        int inputNum = getNumber(scanner, list.length);
        //System.out.printf("Picked `%s`\n", list[inputNum]);
        return list[inputNum - 1];
    }

    public static GameData getGame(Scanner scanner, GameData[] games) throws IOException {
        String prompt = "Pick a game from the list: ";
        ArrayList<String> list = new ArrayList<>();
        for (GameData game : games) {
            list.add("`%s` - White: %s, Black: %s".formatted(game.gameName(), game.whiteUsername(), game.blackUsername()));
        }
        String choice = getFromList(scanner, prompt, list.toArray(new String[0]));
        return games[list.indexOf(choice)];
    }
}
