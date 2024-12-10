package server;

import chess.ChessBoard;
import chess.ChessGame;
import chess.InvalidMoveException;
import com.google.gson.Gson;
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
import java.util.ArrayList;
import java.util.HashMap;

@WebSocket
public class WSServer {
    static final Gson SERIALIZER = new Gson();
    static final SqlAuthDAO AUTH_DAO = SqlAuthDAO.getInstance();
    static final SqlGameDAO GAME_DAO = SqlGameDAO.getInstance();

    record Client(String authToken, RemoteEndpoint client) {}
    record GameClients(Client white, Client black, ArrayList<Client> observers) {}
    private final HashMap<Integer, GameClients> ALL_CLIENTS = new HashMap<>();

    @OnWebSocketMessage
    public void onMessage(Session session, String message) throws Exception {
        try {
            UserGameCommand command = SERIALIZER.fromJson(message, UserGameCommand.class);
            String username = getUsername(command.getAuthToken());
            //saveSession(command.getGameID(), session);
            switch (command.getCommandType()) {
                case CONNECT -> connect(session, username, (ConnectCommand) command);
                case MAKE_MOVE -> move(session, username, (MakeMoveCommand) command);
                case LEAVE -> leave(session, username, command);
                case RESIGN -> resign(session, username, command);
            }

        } catch (Exception e) {
            sendMessage(session.getRemote(), new ServerMessage(ServerMessage.ServerMessageType.ERROR));
        }
        System.out.printf("Received: %s", message);
        session.getRemote().sendString("WebSocket response: " + message);
    }

    private String getUsername(String authToken) {
        try {
            return AUTH_DAO.getUsername(authToken);
        } catch (DataAccessException e) {
            throw new RuntimeException(e);
        }
    }

    private ConnectCommand.CONNECTION_TYPE getConnectionType(int gameID, String authToken) {
        GameClients clients = ALL_CLIENTS.get(gameID);
        if (clients.white.authToken.equals(authToken)) {
            return ConnectCommand.CONNECTION_TYPE.WHITE;
        } else if (clients.black.authToken.equals(authToken)) {
            return ConnectCommand.CONNECTION_TYPE.BLACK;
        }
        for (Client observer : clients.observers) {
            if (observer.authToken.equals(authToken)) {
                return ConnectCommand.CONNECTION_TYPE.OBSERVER;
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
        try {
            client.sendString(SERIALIZER.toJson(message));
        } catch (IOException e) {
            System.out.println("Failed to send message: " + message);
        }
    }

    private void sendMessageAll(int gameID, ServerMessage message) {
        GameClients allClients = ALL_CLIENTS.get(gameID);
        sendMessage(allClients.black.client, message);
        sendMessage(allClients.white.client, message);
        for (Client observer : allClients.observers) {
            sendMessage(observer.client, message);
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
        switch (command.getConnectionType()) {
            case BLACK:
                GameClients newBlack = new GameClients(clients.white, new Client(command.getAuthToken(), session.getRemote()), clients.observers);
                ALL_CLIENTS.put(command.getGameID(), newBlack);
                break;
            case WHITE:
                GameClients newWhite = new GameClients(clients.white, new Client(command.getAuthToken(), session.getRemote()), clients.observers);
                ALL_CLIENTS.put(command.getGameID(), newWhite);
                break;
            case OBSERVER:
                clients.observers.add(new Client(command.getAuthToken(), session.getRemote()));
                break;
        }

        // Server sends a LOAD_GAME message to client
        GameData game = getGame(session.getRemote(), command.getGameID());
        if (game == null) {
            return;
        }
        LoadGameMessage loaded = new LoadGameMessage(game);
        sendMessage(session.getRemote(), loaded);

        // Server sends a NOTIFICATION message to all other clients in that game
        String message = "Received " + command + " from " + username;
        NotificationMessage connected = new NotificationMessage(message);
        sendMessageAll(command.getGameID(), connected);
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

        // Game is updated to represent move
        GameData updatedGame = new GameData(currentGame.gameID(), currentGame.whiteUsername(), currentGame.blackUsername(), currentGame.gameName(), chessGame);
        try {
            GAME_DAO.updateGame(updatedGame);
        } catch (DataAccessException e) {
            sendMessage(session.getRemote(), new ErrorMessage("Error: failed to update game"));
            return;
        }

        // Server sends a LOAD_GAME message to all clients
        sendMessageAll(command.getGameID(), new LoadGameMessage(updatedGame));

        // Server sends a NOTIFICATION message to all clients informing them what move was made
        ChessBoard board = updatedGame.game().getBoard();
        String message = "%s moved %s at %s to %s".formatted(
                username,
                board.getPiece(command.getMove().getEndPosition()).getPieceType().toString().toLowerCase(),
                command.getMove().getStartPosition(),
                command.getMove().getEndPosition()
        );
        sendMessageAll(command.getGameID(), new NotificationMessage(message));

        // If the move results in check, checkmate, or stalemate, the server sends a notification to all clients
        ConnectCommand.CONNECTION_TYPE connectionType = getConnectionType(command.getGameID(), command.getAuthToken());
        ChessGame.TeamColor color;
        if (connectionType == ConnectCommand.CONNECTION_TYPE.WHITE) {
            color = ChessGame.TeamColor.BLACK;
        } else {
            color = ChessGame.TeamColor.WHITE;
        }
        if (updatedGame.game().isInCheckmate(color)) {
            sendMessageAll(command.getGameID(), new NotificationMessage("Checkmate! " + username + " has won the game!"));
        } else if (updatedGame.game().isInStalemate(color)) {
            sendMessageAll(command.getGameID(), new NotificationMessage(username + " has caused a stalemate! It is a draw!"));
        } else if (updatedGame.game().isInCheck(color)) {
            sendMessageAll(command.getGameID(), new NotificationMessage("Check!")); //TODO: username of other player
        }
    }

    /**
     * leave
     * 1. If a player is leaving, then the game is updated to remove the root client. Game is updated in the database.
     * 2. Server sends a Notification message to all other clients in that game informing them that the root client left. This applies to both players and observers.
     * @param session connection to client
     * @param username username of client leaving
     * @param command command with type LEAVE
     */
    private void leave(Session session, String username, UserGameCommand command) {
        // Game is updated to remove client
        GameData previousGame;
        try {
            previousGame = GAME_DAO.getGame(command.getGameID());
        } catch (DataAccessException e) {
            sendMessage(session.getRemote(), new ErrorMessage("Error: game not found"));
            return;
        }
        GameData afterGame;
        if (previousGame.whiteUsername().equals(username)) {
            afterGame = new GameData(previousGame.gameID(), null, previousGame.blackUsername(), previousGame.gameName(), previousGame.game());
        } else {
            afterGame = new GameData(previousGame.gameID(), previousGame.whiteUsername(), null, previousGame.gameName(), previousGame.game());
        }
        try {
            GAME_DAO.updateGame(afterGame);
        } catch (DataAccessException e) {
            sendMessage(session.getRemote(), new ErrorMessage("Error: unable to update game"));
            return;
        }

        // Notification sent to all players
        sendMessageAll(command.getGameID(), new NotificationMessage((username + " left the game")));
    }

    private void resign(Session session, String username, UserGameCommand command) {

        //TODO implement resign
        // Server marks game as over

        NotificationMessage gameOver = new NotificationMessage(username + " resigned");
        sendMessage(session.getRemote(), gameOver);
    }
}