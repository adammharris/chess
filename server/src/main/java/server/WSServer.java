package server;

import com.google.gson.Gson;
import dataaccess.DataAccessException;
import dataaccess.SqlAuthDAO;
import dataaccess.SqlGameDAO;
import model.GameData;
import org.eclipse.jetty.websocket.api.annotations.*;
import org.eclipse.jetty.websocket.api.*;
import websocket.commands.UserGameCommand;
import websocket.messages.ServerMessage;

import java.io.IOException;

@WebSocket
public class WSServer {
    static final Gson SERIALIZER = new Gson();
    static final SqlAuthDAO AUTH_DAO = SqlAuthDAO.getInstance();
    static final SqlGameDAO GAME_DAO = SqlGameDAO.getInstance();

    @OnWebSocketMessage
    public void onMessage(Session session, String message) throws Exception {
        try {
            UserGameCommand command = SERIALIZER.fromJson(message, UserGameCommand.class);
            String username = getUsername(command.getAuthToken());
            //saveSession(command.getGameID(), session);
            switch (command.getCommandType()) {
                case CONNECT -> connect(session, username, command);
                case MAKE_MOVE -> move(session, username, command);
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

    private void sendMessage(RemoteEndpoint client, ServerMessage message) {
        try {
            client.sendString(message.toString());
        } catch (IOException e) {
            System.out.println("Failed to send message: " + message);
        }
    }

    private void connect(Session session, String username, UserGameCommand command) {
        // TODO implement connect
        try {
            session.getRemote().sendString(username + command);
        } catch (IOException e) {
            System.out.println("Failed to send message: " + username + command);
        }
    }

    private void move(Session session, String username, UserGameCommand command) {
        //TODO implement move
        try {
            session.getRemote().sendString(username + command);
        } catch (IOException e) {
            System.out.println("Failed to send message: " + username + command);
        }
    }

    private void leave(Session session, String username, UserGameCommand command) {
        // TODO implement leave
        GameData previousGame;
        try {
            previousGame = GAME_DAO.getGame(command.getGameID());
        } catch (DataAccessException e) {
            throw new RuntimeException(e);
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
            throw new RuntimeException(e);
        }
        try {
            session.getRemote().sendString("Removed %s from game named %s with ID %s".formatted(username, afterGame.gameName(), afterGame.gameID()));
        } catch (IOException e) {
            System.out.println("Failed to send message: " + e.getMessage());
        }
    }

    private void resign(Session session, String username, UserGameCommand command) {
        //TODO implement resign
        try {
            session.getRemote().sendString(username + command);
        } catch (IOException e) {
            System.out.println("Failed to send message: " + username + command);
        }
    }
}