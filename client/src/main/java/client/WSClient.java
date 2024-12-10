package client;

import chess.ChessMove;
import com.google.gson.Gson;
import ui.TextGraphics;
import websocket.commands.MakeMoveCommand;
import websocket.commands.UserGameCommand;
import websocket.messages.ErrorMessage;
import websocket.messages.LoadGameMessage;
import websocket.messages.NotificationMessage;
import websocket.messages.ServerMessage;

import javax.websocket.*;
import java.io.IOException;
import java.net.URI;

public class WSClient extends Endpoint {
    public Session session;
    private static final Gson SERIALIZER = new Gson();

    public WSClient() throws Exception {
        URI uri = new URI("ws://localhost:8080/ws");
        WebSocketContainer container = ContainerProvider.getWebSocketContainer();
        this.session = container.connectToServer(this, uri);
        this.session.addMessageHandler((MessageHandler.Whole<String>) message -> {
            //System.out.println(message);
            ServerMessage serverMessage = SERIALIZER.fromJson(message, ServerMessage.class);
            switch (serverMessage.getServerMessageType()) {
                case ERROR:
                    handleError(SERIALIZER.fromJson(message, ErrorMessage.class));
                    break;
                case LOAD_GAME:
                    handleLoad(SERIALIZER.fromJson(message, LoadGameMessage.class));
                    break;
                case NOTIFICATION:
                    handleNotification(SERIALIZER.fromJson(message, NotificationMessage.class));
                    break;
            }
        });
    }

    private void handleError(ErrorMessage message) {
        System.out.println(message.getErrorMessage());
    }

    private void handleLoad(LoadGameMessage message) {
        // different orientations?
        System.out.println(TextGraphics.constructBoard(message.getGame().game().getBoard(), true));
    }

    private void handleNotification(NotificationMessage message) {
        System.out.println(message.getMessage());
    }

    public void send(UserGameCommand command) {
        try {
            this.session.getBasicRemote().sendText(SERIALIZER.toJson(command));
        } catch (IOException e) {
            System.out.println("Sending command " + command + " failed!");
        } catch (IllegalStateException e) {
            System.out.println("Unfortunately, the connection has closed! Please restart the game.");
        }
    }

    public void onOpen(Session session, EndpointConfig endpointConfig) {
    }

    public void move(String authToken, ChessMove move, int gameID) {
        MakeMoveCommand command = new MakeMoveCommand(authToken, gameID, move);
        send(command);
    }

    public void leave(String authToken, int gameID) {
        UserGameCommand command = new UserGameCommand(UserGameCommand.CommandType.LEAVE, authToken, gameID);
        send(command);
    }

    public void resign(String authToken, int gameID) {
        UserGameCommand command = new UserGameCommand(UserGameCommand.CommandType.RESIGN, authToken, gameID);
        send(command);
    }
}