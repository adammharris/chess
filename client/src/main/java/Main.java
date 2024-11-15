import ui.Game;
import java.util.Scanner;
import server.Server;

public class Main {
    public static void main(String[] args) {
        Server server = new Server();
        int port = server.run(8080);
        Game.start(port, new Scanner(System.in));
        server.stop();
    }
}