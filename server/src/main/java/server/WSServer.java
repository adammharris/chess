package server;

import chess.ChessBoard;
import chess.ChessGame;
import chess.InvalidMoveException;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import dataaccess.DataAccessException;
import dataaccess.SqlAuthDAO;
import dataaccess.SqlGameDAO;
import model.GameData;
import org.eclipse.jetty.websocket.api.annotations.*;
import org.eclipse.jetty.websocket.api.*;
import websocket.commands.ConnectCommand;
import websocket.commands.MakeMoveCommand;
import websocket.commands.UserGameCommand;
import websocket.messages.ErrorMessage;
import websocket.messages.LoadGameMessage;
import websocket.messages.NotificationMessage;
import websocket.messages.ServerMessage;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;

@WebSocket
public class WSServer {
    private static final Gson SERIALIZER = new Gson();
    private static final SqlAuthDAO AUTH_DAO = SqlAuthDAO.getInstance();
    private static final SqlGameDAO GAME_DAO = SqlGameDAO.getInstance();

    private record Client(String authToken, RemoteEndpoint client) {}
    private record GameClients(Client white, Client black, HashSet<Client> observers) {}
    private final static HashMap<Integer, GameClients> ALL_CLIENTS = new HashMap<>();
    private final static HashSet<Integer> GAMES_ENDED = new HashSet<>();

    @OnWebSocketMessage
    public void onMessage(Session session, String message) {
        try {
            UserGameCommand command = SERIALIZER.fromJson(message, UserGameCommand.class);
            String username = getUsername(session.getRemote(), command.getAuthToken());
            if (username == null) {
                return;
            }
            switch (command.getCommandType()) {
                case CONNECT -> connect(session, username, SERIALIZER.fromJson(message, ConnectCommand.class));
                case MAKE_MOVE -> move(session, username, SERIALIZER.fromJson(message, MakeMoveCommand.class));
                case LEAVE -> leave(session, username, command);
                case RESIGN -> resign(session, username, command);
            }

        } catch (JsonSyntaxException e) {
            sendMessage(session.getRemote(), new ErrorMessage("Invalid JSON: " + message));
        }
        //System.out.printf("Server received: %s", message);
        //session.getRemote().sendString("WebSocket response: " + message);
    }

    private String getUsername(RemoteEndpoint client, String authToken) {
        try {
            return AUTH_DAO.getUsername(authToken);
        } catch (DataAccessException e) {
            sendMessage(client, new ErrorMessage("Error: invalid user"));
            return null;
        }
    }

    private ConnectCommand.ConnectionType getConnectionType(int gameID, String authToken) {
        GameClients clients = ALL_CLIENTS.get(gameID);
        if (clients.white() != null) {
            if (clients.white().authToken().equals(authToken)) {
                return ConnectCommand.ConnectionType.WHITE;
            }
        }
        if (clients.black() != null) {
            if (clients.black().authToken().equals(authToken)) {
                return ConnectCommand.ConnectionType.BLACK;
            }
        }
        for (Client observer : clients.observers()) {
            if (observer.authToken().equals(authToken)) {
                return ConnectCommand.ConnectionType.OBSERVER;
            }
        }
        return null;
    }

    private GameData getGame(RemoteEndpoint client, int gameID) {
        try {
            return GAME_DAO.getGame(gameID);
        } catch (DataAccessException e) {
            sendMessage(client, new ErrorMessage("Error: game not found"));
            return null;
        }
    }

    private void sendMessage(RemoteEndpoint client, ServerMessage message) {
        final String json;
        try {
            json = SERIALIZER.toJson(message);
        } catch (JsonSyntaxException e) {
            System.out.println("Invalid json: " + e.getMessage());
            return;
        }
        try {
            client.sendString(json);
            if (message.getServerMessageType() == ServerMessage.ServerMessageType.LOAD_GAME) {
                System.out.println("Sent message of type " + message.getServerMessageType() + " to " + client);
            } else {
                System.out.println("Sent message of type " + message.getServerMessageType() + ": " + json);
            }

        } catch (IOException e) {
            System.out.println("Connection timed out: " + e.getMessage());
        } catch (Exception e) {
            System.out.println("Failed to send message: " + json);
        }
    }

    private void sendMessageAll(int gameID, ServerMessage message, String except) {
        GameClients allClients = ALL_CLIENTS.get(gameID);
        if (allClients.black != null) {
            if (!allClients.black.authToken.equals(except)) {
                sendMessage(allClients.black.client, message);
            }
        }
        if (allClients.white != null) {
            if (!allClients.white.authToken.equals(except)) {
                sendMessage(allClients.white.client, message);
            }

        }
        for (Client observer : allClients.observers) {
            if (!observer.authToken.equals(except)) {
                sendMessage(observer.client, message);
            }
        }
    }

    /**
     * <p>1. Server sends a LOAD_GAME message back to the root client.</p>
     * <p>2. Server sends a NOTIFICATION message to all other clients in that game
     * informing them the root client connected to the game, either as a player
     * (in which case their color must be specified) or as an observer.</p>
     * @param session connection to current client
     * @param username client's username
     * @param command command with type CONNECT
     */
    private void connect(Session session, String username, ConnectCommand command) {
        // Updates client database
        GameClients clients = ALL_CLIENTS.get(command.getGameID());
        if (clients == null) {
            ALL_CLIENTS.put(command.getGameID(), new GameClients(null, null, new HashSet<>()));
            clients = ALL_CLIENTS.get(command.getGameID());
        }
        switch (command.getConnectionType()) {
            case BLACK:
                GameClients newBlack = new GameClients(clients.white, new Client(command.getAuthToken(), session.getRemote()), clients.observers);
                ALL_CLIENTS.put(command.getGameID(), newBlack);
                break;
            case WHITE:
                GameClients newWhite = new GameClients(new Client(command.getAuthToken(), session.getRemote()), clients.black, clients.observers);
                ALL_CLIENTS.put(command.getGameID(), newWhite);
                break;
            case OBSERVER:
                clients.observers.add(new Client(command.getAuthToken(), session.getRemote()));
                break;
            case null:
                if (clients.white == null) {
                    GameClients nw = new GameClients(new Client(command.getAuthToken(), session.getRemote()), clients.black, clients.observers);
                    ALL_CLIENTS.put(command.getGameID(), nw);
                } else if (clients.black == null) {
                    GameClients nb = new GameClients(clients.white, new Client(command.getAuthToken(), session.getRemote()), clients.observers);
                    ALL_CLIENTS.put(command.getGameID(), nb);
                } else {
                    clients.observers.add(new Client(command.getAuthToken(), session.getRemote()));
                }
                break;
        }

        // Server sends a LOAD_GAME message to client
        GameData game = getGame(session.getRemote(), command.getGameID());
        if (game == null) {
            return;
        }
        LoadGameMessage loaded = new LoadGameMessage(game);
        sendMessage(session.getRemote(), loaded);
        //sendMessage(session.getRemote(), new NotificationMessage("Joined game!"));

        // Server sends a NOTIFICATION message to all other clients in that game
        String message = "Received " + command + " from " + username;
        NotificationMessage connected = new NotificationMessage(message);
        sendMessageAll(command.getGameID(), connected, command.getAuthToken());
    }

    /**
     * 1. Server verifies the validity of the move.
     * 2. Game is updated to represent the move. Game is updated in the database.
     * 3. Server sends a LOAD_GAME message to all clients in the game (including the root client) with an updated game.
     * 4. Server sends a Notification message to all other clients in that game informing them what move was made.
     * 5. If the move results in check, checkmate or stalemate the server sends a Notification message to all clients.
     * @param session connection to client
     * @param username username of client
     * @param command command with type MAKE_MOVE
     */
    private void move(Session session, String username, MakeMoveCommand command) {
        if (GAMES_ENDED.contains(command.getGameID())) {
            sendMessage(session.getRemote(), new ErrorMessage("Error: " + username + " attempted to move after game ended"));
        }
        // Server verifies validity of the move
        GameData currentGame = getGame(session.getRemote(), command.getGameID());
        if (currentGame == null) {
            return;
        }
        ChessGame chessGame = currentGame.game();
        try {
            chessGame.makeMove(command.getMove());
        } catch (InvalidMoveException e) {
            sendMessage(session.getRemote(), new ErrorMessage("Error: invalid move"));
            return;
        }
        ConnectCommand.ConnectionType connectionType = getConnectionType(command.getGameID(), command.getAuthToken());
        ChessGame.TeamColor color;
        if (connectionType == ConnectCommand.ConnectionType.WHITE) {
            color = ChessGame.TeamColor.BLACK;
        } else {
            color = ChessGame.TeamColor.WHITE;
        }
        boolean isWhite = false;
        boolean isBlack = false;
        GameClients allClients = ALL_CLIENTS.get(command.getGameID());
        if (allClients.white() != null) {
            if (allClients.white.authToken.equals(command.getAuthToken())) {
                isWhite = true;
            }
        }
        if (allClients.black() != null) {
            if (allClients.black.authToken.equals(command.getAuthToken())) {
                isBlack = true;
            }
        }
        if (isWhite
                && (connectionType != ConnectCommand.ConnectionType.WHITE
                || chessGame.getBoard().getPiece(command.getMove().getEndPosition()).getTeamColor() != ChessGame.TeamColor.WHITE
        )) {
            sendMessage(session.getRemote(), new ErrorMessage("Error: " + username + "is white, but tried to move someone else's piece"));
        }
        if (isBlack
                && (connectionType != ConnectCommand.ConnectionType.BLACK
                || chessGame.getBoard().getPiece(command.getMove().getEndPosition()).getTeamColor() != ChessGame.TeamColor.BLACK)) {
            sendMessage(session.getRemote(), new ErrorMessage("Error: " + username + "is black, but tried to move someone else's piece"));
        }
        if (connectionType == ConnectCommand.ConnectionType.OBSERVER) {
            sendMessage(session.getRemote(), new ErrorMessage("Error: " + username + " tried to move, but is an observer"));
        }

        // Game is updated to represent move
        GameData updatedGame = new GameData(
                currentGame.gameID(),
                currentGame.whiteUsername(),
                currentGame.blackUsername(),
                currentGame.gameName(),
                chessGame
        );
        try {
            GAME_DAO.updateGame(updatedGame);
        } catch (DataAccessException e) {
            sendMessage(session.getRemote(), new ErrorMessage("Error: failed to update game"));
            return;
        }

        // Server sends a LOAD_GAME message to all clients
        sendMessageAll(command.getGameID(), new LoadGameMessage(updatedGame), "");

        // Server sends a NOTIFICATION message to all clients informing them what move was made
        ChessBoard board = updatedGame.game().getBoard();
        String message = "%s moved %s at %s to %s".formatted(
                username,
                board.getPiece(command.getMove().getEndPosition()).getPieceType().toString().toLowerCase(),
                command.getMove().getStartPosition(),
                command.getMove().getEndPosition()
        );
        sendMessageAll(command.getGameID(), new NotificationMessage(message), command.getAuthToken());

        // If the move results in check, checkmate, or stalemate, the server sends a notification to all clients

        if (updatedGame.game().isInCheckmate(color)) {
            sendMessageAll(command.getGameID(), new NotificationMessage("Checkmate! " + username + " has won the game!"), "");
        } else if (updatedGame.game().isInStalemate(color)) {
            sendMessageAll(command.getGameID(), new NotificationMessage(username + " has caused a stalemate! It is a draw!"), "");
        } else if (updatedGame.game().isInCheck(color)) {
            sendMessageAll(command.getGameID(), new NotificationMessage("Check!"), "");
        }
    }

    /**
     * leave
     * 1. If a player is leaving, then the game is updated to remove the root client. Game is updated in the database.
     * 2. Server sends a Notification message to all other clients in that game informing them that the root client left.
     * This applies to both players and observers.
     * @param session connection to client
     * @param username username of client leaving
     * @param command command with type LEAVE
     */
    private void leave(Session session, String username, UserGameCommand command) {
        // Game is updated to remove client
        GameData previousGame = getGame(session.getRemote(), command.getGameID());
        if (previousGame == null) {
            return;
        }
        String whiteUsername = previousGame.whiteUsername();
        String blackUsername = previousGame.blackUsername();
        ConnectCommand.ConnectionType connectionType = getConnectionType(previousGame.gameID(), command.getAuthToken());
        if (connectionType == ConnectCommand.ConnectionType.WHITE) {
            whiteUsername = "null";
        } else if (connectionType == ConnectCommand.ConnectionType.BLACK) {
            blackUsername = "null";
        }
        GameData afterGame = new GameData(
                previousGame.gameID(),
                whiteUsername,
                blackUsername,
                previousGame.gameName(),
                previousGame.game()
        );
        try {
            //GAME_DAO.
            GAME_DAO.updateGame(afterGame);
        } catch (DataAccessException e) {
            sendMessage(session.getRemote(), new ErrorMessage("Error: unable to update game"));
            return;
        }

        GameClients clients = ALL_CLIENTS.get(command.getGameID());
        if (clients.black() != null) {
            if (command.getAuthToken().equals(clients.black().authToken())) {
                clients = new GameClients(clients.white(), null, clients.observers());
            }
        }
        if (clients.white() != null) {
            if (command.getAuthToken().equals(clients.white().authToken())) {
                clients = new GameClients(null, clients.black(), clients.observers());
            }
        }
        clients.observers().removeIf(observer -> observer.authToken().equals(command.getAuthToken()));
        ALL_CLIENTS.put(command.getGameID(), new GameClients(clients.white(), clients.black(), clients.observers()));

        // Notification sent to all players
        sendMessageAll(command.getGameID(), new NotificationMessage((username + " left the game")), command.getAuthToken());
    }

    private void resign(Session session, String username, UserGameCommand command) {
        if (getConnectionType(command.getGameID(), command.getAuthToken()) == ConnectCommand.ConnectionType.OBSERVER) {
            sendMessage(session.getRemote(), new ErrorMessage("Error: observer attempted to resign"));
            return;
        }
        if (GAMES_ENDED.contains(command.getGameID())) {
            sendMessage(session.getRemote(), new ErrorMessage("Error: attempted to resign after game ended"));
            return;
        }
        GAMES_ENDED.add(command.getGameID());

        sendMessageAll(command.getGameID(), new NotificationMessage(username + " resigned."), "");
    }
}