package client;

import chess.ChessMove;

import javax.websocket.*;
import java.net.URI;

public class WSClient extends Endpoint {
    public Session session;

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

    public void move(String authToken, ChessMove move) {
        //TODO implement move
    }

    public void leave(String authToken) {
        // TODO implement leave
    }

    public void resign(String authToken) {
        //TODO implement resign
    }
}