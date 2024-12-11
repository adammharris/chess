package ui;

import chess.*;
import client.ServerFacade;
import model.GameData;

import java.io.IOException;
import java.util.*;
import java.util.function.Consumer;
import java.util.logging.Level;
import java.util.logging.LogRecord;

public class Game {
    private static ServerFacade server = null;
    private static boolean keepGoing = true;
    private static Consumer<Scanner> currentFunction;
    private static final java.util.logging.Logger LOGGER = java.util.logging.Logger.getLogger("global");

    private static String authToken = "";
    private final static ArrayList<GameData> GAMES = new ArrayList<>();
    private static GameData currentGame;
    private static String currentUsername;
    private static ChessGame.TeamColor currentColor;

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



    private static void register(Scanner scanner) {
        System.out.print("Time to register!\n");
        try {
            String username = GameInput.getString(scanner, "Please enter a username: ");
            String password  = GameInput.getString(scanner, "Please enter a password: ");
            String email = GameInput.getString(scanner, "Please enter an email: ");
            String confirmPassword = GameInput.getString(scanner, "Great! Now just confirm your password: ");
            if (!password.equals(confirmPassword)) {
                throw new IOException("Passwords did not match!");
            }
            currentUsername = username;
            authToken = server.register(currentUsername, password, email);
            currentFunction = (scan) -> postlogin(scanner);
        } catch (IOException e) {
            System.out.print("Invalid input! Please try again.\n");
            currentFunction = (scan) -> prelogin(scanner);
        }
    }

    private static void login(Scanner scanner) {
        System.out.println("Time to login!");
        try {
            String username = GameInput.getString(scanner, "Please enter username: ");
            currentUsername = username;
            String password = GameInput.getString(scanner, "Please enter password: ");
            authToken = server.login(username, password);
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
        System.out.printf("[Logged in as %s] >>> ", currentUsername);
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
                try {
                    server.logout(authToken);
                    System.out.println("Logged out!");
                } catch (IOException e) {
                    LOGGER.log(new LogRecord(Level.ALL, e.getMessage()));
                    System.out.println("Logout failed!");
                }
                authToken = "";
                currentUsername = null;
                currentFunction = (scan) -> prelogin(scanner);
                break;
            case "create":
                try {
                    String gameName = GameInput.getGameName(scanner);
                    server.createGame(authToken, gameName);
                    refreshGames();
                    System.out.printf("Created game %s!\n", gameName);
                } catch (IOException e) {
                    LOGGER.log(new LogRecord(Level.ALL, e.getMessage()));
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
                    currentGame = GameInput.getGame(scanner, GAMES.toArray(new GameData[0]));
                    setupWebsocket();
                    currentColor = GameInput.getColor(scanner);
                    String colorString = (currentColor == ChessGame.TeamColor.WHITE) ? "WHITE" : "BLACK";
                    System.out.println(colorString);

                    server.joinGame(authToken, colorString, currentGame.gameID());
                    System.out.println("Joined game!");
                    currentFunction = (scan) -> gameplay(scanner);
                } catch (IOException e) {
                    LOGGER.log(Level.ALL, e.getMessage());
                    System.out.println("Join game failed!");
                }
                break;
            case "observe":
                try {
                    currentGame = GameInput.getGame(scanner, GAMES.toArray(new GameData[0]));
                    setupWebsocket();
                    server.observeGame(authToken, currentGame.gameID());
                    currentFunction = (scan) -> observe(scanner);
                } catch (IOException e) {
                    LOGGER.log(new LogRecord(Level.ALL, e.getMessage()));
                    System.out.println("Invalid input!");
                }
                break;
            default:
                System.out.println("Command not available. Type `help` for available commands.");
                break;
        }
    }

    private static void setupWebsocket() {
        if (currentGame == null) {
            throw new RuntimeException("`Game::setupWebsocket` called, but there is no game!");
        }
        try {
            server.setupWebsocket();
            //server.sendMessage("Websocket connected!");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static void gameplay(Scanner scanner) {
        System.out.printf("[Playing game %s] >>> ", currentGame.gameName());
        String input = scanner.next();
        switch (input) {
            case "help":
                System.out.print("""
                        Command\t\tDescription
                        Help\t\tDisplays text informing the user what actions they can take.
                        Draw\t\tRedraws the chess board
                        Leave\t\tExit the game (you can join back in later)
                        Move\t\tMove a chess piece
                        Resign\t\tForfeit the game and exit
                        Highlight\tPick a chess piece and highlight all the possible moves it can make
                        """);
                break;
            case "draw":
                loadGame();

                break;
            case "leave":
                server.leave(authToken, currentGame.gameID());
                System.out.println("Left game!");
                currentFunction = (scan) -> postlogin(scanner);
                break;
            case "move":
                ChessPosition startPosition;
                ChessPosition endPosition;
                ChessPiece piece;
                try {
                    System.out.println("Which piece would you like to move?");
                    startPosition = GameInput.getChessPosition(scanner);
                    piece = currentGame.game().getBoard().getPiece(startPosition);
                    if (piece == null) {
                        throw new IOException("Piece not found!");
                    }
                } catch (IOException e) {
                    LOGGER.log(new LogRecord(Level.ALL, e.getMessage()));
                    System.out.println("There is no piece there!");
                    break;
                }

                try {
                    System.out.println("Where would you like to move it?");
                    endPosition = GameInput.getChessPosition(scanner);
                } catch (IOException e) {
                    LOGGER.log(new LogRecord(Level.ALL, e.getMessage()));
                    System.out.println("Invalid input!");
                    break;
                }
                ChessPiece.PieceType pieceType = null;
                if (piece.getPieceType() == ChessPiece.PieceType.PAWN
                        && ((piece.getTeamColor() == ChessGame.TeamColor.WHITE
                        & Objects.requireNonNull(endPosition).getRow() == 8)
                        ) || (piece.getTeamColor() == ChessGame.TeamColor.BLACK
                        && Objects.requireNonNull(endPosition).getRow() == 1
                    )) {
                    try {
                        System.out.println("Your pawn can be promoted!");
                        pieceType = GameInput.getPromotion(scanner);
                    } catch (IOException e) {
                        LOGGER.log(new LogRecord(Level.ALL, e.getMessage()));
                        System.out.println("Invalid promotion");
                    }
                }
                ChessMove move;
                if (pieceType != null) {
                    move = new ChessMove(startPosition, endPosition, pieceType);
                } else {
                    move = new ChessMove(startPosition, endPosition);
                }

                server.move(authToken, move, currentGame.gameID());
                break;
            case "resign":
                System.out.println("Are you sure you want to resign?");
                try {
                    boolean isYes = GameInput.getBoolean(scanner);
                    if (isYes) {
                        server.resign(authToken, currentGame.gameID());
                        System.out.println("Resigned from game!");
                        currentGame = null;
                        currentFunction = (scan) -> postlogin(scanner);
                    }
                } catch (IOException e) {
                    LOGGER.log(new LogRecord(Level.ALL, e.getMessage()));
                    System.out.println("Invalid input!");
                }
                break;
            case "highlight":
                highlight(scanner);
                break;
            default:
                System.out.println("Command not available. Type `help` for available commands.");
                break;
        }
    }

    private static void observe(Scanner scanner) {
        System.out.printf("[Observing game `%s`] >>> ", currentGame.gameName());
        String input = scanner.next();
        switch (input) {
            case "help":
                System.out.print("""
                        Command\t\tDescription
                        Help\t\tDisplays text informing the user what actions they can take.
                        Draw\t\tRedraws the chess board
                        Leave\t\tGoes back to the previous menu
                        Highlight\tPick a chess piece and highlight all the possible moves it can make
                        """);
                break;
            case "draw":
                try {
                    System.out.println("Please choose a side to view from.");
                    ChessGame.TeamColor color = GameInput.getColor(scanner);
                    loadGame(color);
                } catch (IOException e) {
                    LOGGER.log(new LogRecord(Level.ALL, e.getMessage()));
                    System.out.println("Invalid input!");
                }
                break;
            case "leave":
                System.out.printf("No longer observing `%s`\n", currentGame.gameName());
                server.leave(authToken, currentGame.gameID());
                currentGame = null;
                currentFunction = (scan) -> postlogin(scanner);
                break;
            case "highlight":
                highlight(scanner);
                break;
        }
    }

    private static void highlight(Scanner scanner) {
        System.out.println("What chess piece would you like to see valid moves for?");
        ChessPosition position;
        try {
            position = GameInput.getChessPosition(scanner);
        } catch (IOException e) {
            LOGGER.log(new LogRecord(Level.ALL, e.getMessage()));
            System.out.println("Invalid input!");
            return;
        }
        System.out.printf("Highlighted moves for chess piece %s:\n", position);
        //  draw highlighted moves!!!
        ChessPiece piece = currentGame.game().getBoard().getPiece(position);
        ArrayList<ChessMove> highlighted_moves = new ArrayList<>(piece.pieceMoves(currentGame.game().getBoard(), position));
        ArrayList<ChessPosition> highlights_list = new ArrayList<>();
        for (ChessMove move: highlighted_moves) {
            highlights_list.add(move.getEndPosition());
        }
        highlights_list.add(position);

        ChessPosition[] highlights = new ChessPosition[highlights_list.size()];

        if (currentColor == null) {
            loadGame(piece.getTeamColor(), highlights_list.toArray(highlights));
        } else {
            loadGame(currentColor, highlights_list.toArray(highlights));
        }

    }

    public static void loadGame(ChessGame.TeamColor color, ChessPosition[] highlights, GameData game) {
        currentGame = game;
        try {
            refreshGames();
        } catch (IOException e) {
            System.out.println("Failed to load game!");
        }
        String printBoard = TextGraphics.constructBoard(game.game().getBoard(), color == ChessGame.TeamColor.WHITE, highlights);
        System.out.println(printBoard);
    }
    public static void loadGame(ChessGame.TeamColor color, ChessPosition[] highlights) {
        loadGame(color, highlights, currentGame);
    }
    public static void loadGame(GameData game) {
        ChessPosition[] highlights = {};
        loadGame(currentColor, highlights, game);
    }
    public static void loadGame(ChessGame.TeamColor color) {
        ChessPosition[] highlights = {};
        loadGame(color, highlights);
    }
    public static void loadGame() {
        loadGame(currentColor);
        System.out.print(">>> ");
    }

}
