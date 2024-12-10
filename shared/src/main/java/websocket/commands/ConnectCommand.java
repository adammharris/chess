package websocket.commands;

public class ConnectCommand extends UserGameCommand {
    public enum ConnectionType {
        OBSERVER,
        WHITE,
        BLACK
    }
    private final ConnectionType connectionType;
    public ConnectCommand(String authToken, Integer gameID, ConnectionType connectionType) {
        super(CommandType.CONNECT, authToken, gameID);
        this.connectionType = connectionType;
    }
    public ConnectionType getConnectionType() {
        return connectionType;
    }
}