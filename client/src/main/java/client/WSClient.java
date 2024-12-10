package client;

import chess.ChessMove;
import com.google.gson.Gson;
import websocket.commands.UserGameCommand;

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
        this.session.addMessageHandler((MessageHandler.Whole<String>) System.out::println);
    }

    public void send(String msg) throws Exception {
        this.session.getBasicRemote().sendText(msg);
    }

    public void onOpen(Session session, EndpointConfig endpointConfig) {
    }

    public void move(String authToken, ChessMove move, int gameID) {
        //TODO implement move
        UserGameCommand command = new UserGameCommand(UserGameCommand.CommandType.MAKE_MOVE, authToken, gameID);
        try {
            this.session.getBasicRemote().sendText(SERIALIZER.toJson(command));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void leave(String authToken, int gameID) {
        UserGameCommand command = new UserGameCommand(UserGameCommand.CommandType.LEAVE, authToken, gameID);
        try {
            this.session.getBasicRemote().sendText(SERIALIZER.toJson(command));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void resign(String authToken, int gameID) {
        UserGameCommand command = new UserGameCommand(UserGameCommand.CommandType.RESIGN, authToken, gameID);
        try {
            this.session.getBasicRemote().sendText(SERIALIZER.toJson(command));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}