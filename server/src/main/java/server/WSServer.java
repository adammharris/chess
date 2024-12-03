package server;

import com.google.gson.Gson;
import dataaccess.DataAccessException;
import dataaccess.SqlAuthDAO;
import org.eclipse.jetty.websocket.api.annotations.*;
import org.eclipse.jetty.websocket.api.*;
import websocket.commands.UserGameCommand;
import websocket.messages.ServerMessage;

@WebSocket
public class WSServer {
    static final Gson SERIALIZER = new Gson();
    static final SqlAuthDAO AUTH_DAO = SqlAuthDAO.getInstance();

    @OnWebSocketMessage
    public void onMessage(Session session, String message) throws Exception {
        try {
            UserGameCommand command = SERIALIZER.fromJson(message, UserGameCommand.class);
            String username = getUsername(command.getAuthToken());
            //saveSession(command.getGameID(), session);
            switch (command.getCommandType()) {
                case CONNECT -> connect(session, username, command);
                case MAKE_MOVE -> makeMove(session, username, command);
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
        // TODO implement sendMessage
    }

    private void connect(Session session, String username, UserGameCommand command) {
        // TODO implement connect
    }

    private void makeMove(Session session, String username, UserGameCommand command) {
        //TODO implement makeMove
    }

    private void leave(Session session, String username, UserGameCommand command) {
        // TODO implement leave
    }

    private void resign(Session session, String username, UserGameCommand command) {
        //TODO implement resign
    }
}