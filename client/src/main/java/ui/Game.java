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
    private static ServerFacade server = null;
    private static boolean keepGoing = true;
    private static Consumer<Scanner> currentFunction;
    private final static HashMap<String, String> INPUTS = new HashMap<>();
    private static String authToken = "";
    private final static ArrayList<GameData> GAMES = new ArrayList<>();
    private static GameData currentGame;

    private static void refreshGames() throws IOException {
        GAMES.clear();
        GAMES.addAll(List.of(server.listGames(authToken)));
    }

    public static void start(int port, Scanner scanner) {
        server = new ServerFacade(port);
        System.out.println("♕ Welcome to 240 Chess. Type 'help' to get started.");
        currentFunction = (scan) -> prelogin(scanner);
        while (keepGoing) {
            currentFunction.accept(scanner);
        }
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
                        Login\t\tAsks for login information and grants access to chess GAMES.
                        Register\tAsks for registration information, then logs in.
                        """);
                break;
            case "quit":
                System.out.print("Goodbye!\n");
                keepGoing = false;
                break;
            case "register":
                currentFunction = (scan) -> register(scanner);
                break;
            case "login":
                currentFunction = (scan) -> login(scanner);
                break;
            default:
                System.out.print("Command not available. Type `help` for available commands.\n");
        }
    }

    private static void setVariable(Scanner scanner, String key) throws IOException {
        if (key.equals("playerColor")) {
            System.out.print("Please enter player color (`WHITE` or `BLACK`): ");
            INPUTS.put(key, scanner.next());
            if (!(INPUTS.get("playerColor").equals("WHITE") || INPUTS.get("playerColor").equals("BLACK"))) {
                throw new IOException("Invalid input! (Not a player color)");
            }
        } else if (key.equals("number")) {
            System.out.printf("Please enter the number of the game (between 1 and %s): ", GAMES.size());
            INPUTS.put(key, scanner.next());
            try {
                Integer.parseInt(INPUTS.get(key));
            } catch (NumberFormatException e) {
                throw new IOException(e);
            }
        } else {
            System.out.printf("Please enter %s: ", key);
            INPUTS.put(key, scanner.next());
        }
    }

    private static void register(Scanner scanner) {
        System.out.print("Time to register!\n");
        try {
            setVariable(scanner, "username");
            setVariable(scanner, "password");
            setVariable(scanner, "email");
            authToken = server.register(INPUTS.get("username"), INPUTS.get("password"), INPUTS.get("email"));
            currentFunction = (scan) -> postlogin(scanner);
        } catch (IOException e) {
            System.out.print("Invalid input! Please try again.\n");
            currentFunction = (scan) -> prelogin(scanner);
        }
    }

    private static void login(Scanner scanner) {
        System.out.println("Time to login!");
        try {
            setVariable(scanner, "username");
            setVariable(scanner, "password");
            authToken = server.login(INPUTS.get("username"), INPUTS.get("password"));
            System.out.println("Successfully logged in!");
            currentFunction = (scan) -> postlogin(scanner);
        } catch (IOException e) {
            System.out.print("Invalid input! Please try again.\n");
            currentFunction = (scan) -> prelogin(scanner);
        }
    }

    private static void postlogin(Scanner scanner) {
        try {
            refreshGames();
        } catch (IOException e) {
            System.out.println("Failed to login!");
        }
        System.out.printf("[Logged in as %s] >>> ", INPUTS.get("username"));
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
                currentFunction = (scan) -> prelogin(scanner);
                break;
            case "create":
                try {
                    setVariable(scanner, "gameName");
                    server.createGame(authToken, INPUTS.get("gameName"));
                    refreshGames();
                    System.out.printf("Created game %s!\n", INPUTS.get("gameName"));
                } catch (IOException e) {
                    System.out.println("Failed to create game!" + e.getMessage());
                }
                break;
            case "list":
                try {
                    refreshGames();
                } catch (IOException e) {
                    System.out.println("Failed to update list of games!");
                }
                for (GameData game : GAMES) {
                    String name = "%s: \"%s\"".formatted(GAMES.indexOf(game) + 1, game.gameName());
                    System.out.println(EscapeSequences.SET_TEXT_BOLD + name + EscapeSequences.RESET_TEXT_BOLD_FAINT);
                    System.out.printf("\tPlaying as white: %s%n", game.whiteUsername());
                    System.out.printf("\tPlaying as black: %s%n", game.blackUsername());
                }
                break;
            case "play":
                try {
                    setVariable(scanner, "playerColor");
                    setVariable(scanner, "number");
                    gameIndex = Integer.parseInt(INPUTS.get("number"));
                    currentGame = GAMES.get(gameIndex - 1);
                    server.joinGame(authToken, INPUTS.get("playerColor"), currentGame.gameID());
                    System.out.println("Joined game!");
                    currentFunction = (scan) -> gameplay(scanner);
                } catch (Exception e) {
                    System.out.println("Join game failed!");
                }
                break;
            case "observe":
                try {
                    setVariable(scanner, "number");
                    gameIndex = Integer.parseInt(INPUTS.get("number"));
                    currentGame = GAMES.get(gameIndex - 1);
                    currentFunction = (scan) -> gameplay(scanner);
                } catch (Exception e) {
                    System.out.println("Invalid input!");
                }
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
        try {
            server.setupWebsocket();
            server.sendMessage("Websocket connected!");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        System.out.println(TextGraphics.constructBoard(currentGame.game().getBoard(), false));
        System.out.println(TextGraphics.constructBoard(currentGame.game().getBoard(), true));
        currentFunction = (scan) -> postlogin(scanner);
    }
}
