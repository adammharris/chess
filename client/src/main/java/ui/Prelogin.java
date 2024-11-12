package ui;

import client.ServerFacade;

import java.io.IOException;
import java.util.Scanner;

public class Prelogin {
    Scanner scanner;
    ServerFacade server = new ServerFacade();

    public Prelogin(Scanner scanner) {
        this.scanner = scanner;
    }

    public void inputCommand() {
        boolean keepGoing = true;
        while (keepGoing) {
            System.out.print("[LOGGED OUT] >>> ");
            String input = scanner.next();
            switch (input) {
                case "help":
                    System.out.print("""
                            Command\t\tDescription
                            Help\t\tDisplays list of possible commands.
                            Quit\t\tExits the program.
                            Login\t\tAsks for login information and grants access to chess games.
                            Register\tAsks for registration information, then logs in.
                            """);
                    break;
                case "quit":
                    System.out.print("Goodbye!\n");
                    keepGoing = false;
                    break;
                case "register":
                    System.out.print("Time to register! (Not implemented)\n");
                    try {
                        server.register("user", "pass", "email");
                        // Go to Postlogin

                    } catch (IOException e) {
                        //throw new RuntimeException(e);
                        System.out.print("An error occurred! Please try again.\n");
                    }
                    break;
                case "login":
                    System.out.print("Enter credentials: (Not implemented)\n");
                    break;
                default:
                    System.out.print("Command not available. Type `help` for available commands.\n");
            }
        }
    }
}
