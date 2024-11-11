import chess.*;

import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        //var piece = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.PAWN);
        System.out.println("♕ Welcome to 240 Chess. Type 'help' to get started.");

        Prelogin prelogin = new Prelogin(new Scanner(System.in));
        prelogin.inputCommand();
    }
}