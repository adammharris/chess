package websocket.commands;

public class ConnectCommand extends UserGameCommand {
    public enum CONNECTION_TYPE {
        OBSERVER,
        WHITE,
        BLACK
    }
    private final CONNECTION_TYPE CONNECTION_TYPE;
    public ConnectCommand(String authToken, Integer gameID, CONNECTION_TYPE connectionType) {
        super(CommandType.CONNECT, authToken, gameID);
        this.CONNECTION_TYPE = connectionType;
    }
    public CONNECTION_TYPE getCONNECTION_TYPE() {
        return CONNECTION_TYPE;
    }
}
