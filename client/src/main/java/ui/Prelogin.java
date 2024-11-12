package ui;

import client.ServerFacade;
import model.GameData;

import java.io.IOException;
import java.util.HashMap;
import java.util.Scanner;

public class Prelogin {
    private static Scanner scanner;
    private final static ServerFacade server = new ServerFacade();
    private static boolean keepGoing = true;
    private static Runnable currentFunction = Prelogin::inputCommand;
    private final static HashMap<String, String> inputs = new HashMap<>();
    private static String authToken = "";

    public Prelogin(Scanner scanner) {
        Prelogin.scanner = scanner;
    }

    public static void start() {
        while (keepGoing) {
            currentFunction.run();
        }
    }

    public static void inputCommand() {
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
                currentFunction = Prelogin::register;
                break;
            case "login":
                currentFunction = Prelogin::login;
                break;
            default:
                System.out.print("Command not available. Type `help` for available commands.\n");
        }
    }

    private static void setVariable(String key) {
        System.out.printf("Please enter %s: ", key);
        inputs.put(key, scanner.next());
    }

    private static void register() {
        System.out.print("Time to register!\n");
        setVariable("username");
        setVariable("password");
        setVariable("email");
        try {
            authToken = server.register(inputs.get("username"), inputs.get("password"), inputs.get("email"));
            currentFunction = Prelogin::postlogin;
        } catch (IOException e) {
            System.out.print("Invalid input! Please try again.\n");
            currentFunction = Prelogin::inputCommand;
        }
    }

    private static void login() {
        System.out.println("Time to login!");
        setVariable("username");
        setVariable("password");
        try {
            authToken = server.login(inputs.get("username"), inputs.get("password"));
            System.out.println("Successfully logged in!");
            currentFunction = Prelogin::postlogin;
        } catch (IOException e) {
            System.out.print("Invalid input! Please try again.\n");
            currentFunction = Prelogin::inputCommand;
        }
    }

    private static void postlogin() {
        System.out.printf("[Logged in as %s] >>> ", inputs.get("username"));
        String input = scanner.next();
        switch (input) {
            case "help":
                System.out.print("""
                        Command\t\tDescription
                        Help\t\tDisplays list of possible commands.
                        Logout\t\tLogs out the user and returns to previous screen.
                        Create\t\tCreate a chess game so you can join it and start playing!
                        List\t\tLists all the games that currently exist on the server.
                        Play\t\tJoin a created game and start playing!
                        Observe\t\tView a game that is being played!
                        """);
                break;
            case "logout":
                break;
            case "create":
                setVariable("gameName");
                try {
                    int gameID = server.createGame(inputs.get("gameName"));
                } catch (IOException e) {
                    System.out.println("Failed to create game!");
                }
                break;
            case "list":
                try {
                    GameData[] games = server.listGames(authToken);
                    for (GameData game : games) {
                        System.out.println(game);
                    }
                } catch (IOException e) {
                    System.out.println("Not authorized!");
                }
                break;
            case "play":
                break;
            case "observe":
                break;
            default:
                System.out.println("Command not available. Type `help` for available commands.");
                break;

        }
    }
}
