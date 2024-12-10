package websocket.commands;

public class ConnectCommand extends UserGameCommand {
    public enum CONNECTION_TYPE {
        OBSERVER,
        WHITE,
        BLACK
    }
    private final CONNECTION_TYPE connectionType;
    public ConnectCommand(String authToken, Integer gameID, CONNECTION_TYPE connectionType) {
        super(CommandType.CONNECT, authToken, gameID);
        this.connectionType = connectionType;
    }
    public CONNECTION_TYPE getConnectionType() {
        return connectionType;
    }
}
