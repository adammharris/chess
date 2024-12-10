package client;

import chess.ChessMove;
import com.google.gson.Gson;
import websocket.commands.MakeMoveCommand;
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

    public void send(UserGameCommand command) {
        try {
            this.session.getBasicRemote().sendText(SERIALIZER.toJson(command));
        } catch (IOException e) {
            System.out.println("Sending command " + command + " failed!");
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