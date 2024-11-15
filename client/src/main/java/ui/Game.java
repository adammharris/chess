package ui;

import client.ServerFacade;
import model.GameData;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Scanner;
import java.util.function.Consumer;

public class Game {
    private final static ServerFacade server = new ServerFacade();
    private static boolean keepGoing = true;
    private static Consumer<Scanner> currentFunction;
    private final static HashMap<String, String> inputs = new HashMap<>();
    private static String authToken = "";
    private final static ArrayList<GameData> games = new ArrayList<>();
    private static GameData currentGame;

    public static void start(Scanner scanner) {
        System.out.println("♕ Welcome to 240 Chess. Type 'help' to get started.");
        currentFunction = (Scanner) -> prelogin(scanner);
        while (keepGoing) {
            currentFunction.accept(scanner);
        }
        server.stop();
    }

    public static void prelogin(Scanner scanner) {
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
                currentFunction = (Scanner) -> register(scanner);
                break;
            case "login":
                currentFunction = (Scanner) -> login(scanner);
                break;
            default:
                System.out.print("Command not available. Type `help` for available commands.\n");
        }
    }

    private static void setVariable(Scanner scanner, String key) {
        System.out.printf("Please enter %s: ", key);
        inputs.put(key, scanner.next());
    }

    private static void register(Scanner scanner) {
        System.out.print("Time to register!\n");
        setVariable(scanner, "username");
        setVariable(scanner, "password");
        setVariable(scanner, "email");
        try {
            authToken = server.register(inputs.get("username"), inputs.get("password"), inputs.get("email"));
            currentFunction = (Scanner) -> postlogin(scanner);
        } catch (IOException e) {
            System.out.print("Invalid input! Please try again.\n");
            currentFunction = (Scanner) -> prelogin(scanner);
        }
    }

    private static void login(Scanner scanner) {
        System.out.println("Time to login!");
        setVariable(scanner, "username");
        setVariable(scanner, "password");
        try {
            authToken = server.login(inputs.get("username"), inputs.get("password"));
            System.out.println("Successfully logged in!");
            currentFunction = (Scanner) -> postlogin(scanner);
        } catch (IOException e) {
            System.out.print("Invalid input! Please try again.\n");
            currentFunction = (Scanner) -> prelogin(scanner);
        }
    }

    private static void postlogin(Scanner scanner) {
        try {
            games.addAll(List.of(server.listGames(authToken)));
        } catch (IOException e) {
            System.out.println("Failed to login!");
        }
        System.out.printf("[Logged in as %s] >>> ", inputs.get("username"));
        String input = scanner.next();
        int gameIndex;
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
                try {
                    server.logout(authToken);
                    System.out.println("Logged out!");
                } catch (IOException e) {
                    System.out.println("Logout failed!");
                }
                authToken = "";
                currentFunction = (Scanner) -> prelogin(scanner);
                break;
            case "create":
                setVariable(scanner, "gameName");
                try {
                    server.createGame(authToken, inputs.get("gameName"));
                    System.out.printf("Created game %s!", inputs.get("gameName"));
                } catch (IOException e) {
                    System.out.println("Failed to create game!" + e.getMessage());
                }
                break;
            case "list":
                games.clear();
                try {
                    games.addAll(List.of(server.listGames(authToken)));
                } catch (IOException e) {
                    System.out.println("Failed to update list of games!");
                }
                for (GameData game : games) {
                    String name = "%s: \"%s\"".formatted(games.indexOf(game) + 1, game.gameName());
                    System.out.println(EscapeSequences.SET_TEXT_BOLD + name + EscapeSequences.RESET_TEXT_BOLD_FAINT);
                    System.out.printf("\tPlaying as white: %s%n", game.whiteUsername());
                    System.out.printf("\tPlaying as black: %s%n", game.blackUsername());
                }
                break;
            case "play":
                setVariable(scanner, "playerColor");
                setVariable(scanner, "number");
                gameIndex = Integer.parseInt(inputs.get("number"));
                try {
                    currentGame = games.get(gameIndex - 1);
                    server.joinGame(authToken, inputs.get("playerColor"), currentGame.gameID());
                    System.out.println("Joined game!");
                    currentFunction = (Scanner) -> gameplay(scanner);
                } catch (IOException e) {
                    System.out.println("Join game failed!");
                }
                break;
            case "observe":
                setVariable(scanner, "number");
                gameIndex = Integer.parseInt(inputs.get("number"));
                currentGame = games.get(gameIndex - 1);
                currentFunction = (Scanner) -> gameplay(scanner);
                break;
            default:
                System.out.println("Command not available. Type `help` for available commands.");
                break;
        }
    }

    private static void gameplay(Scanner scanner) {
        if (currentGame == null) {
            throw new RuntimeException("`Game::gameplay` called, but there is no game!");
        }
        System.out.println(TextGraphics.constructBoard(currentGame.game().getBoard(), false));
        System.out.println(TextGraphics.constructBoard(currentGame.game().getBoard(), true));
        currentFunction = (Scanner) -> postlogin(scanner); //TODO: implement gameplay
    }
}
