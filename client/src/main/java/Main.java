import ui.Game;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        server.Server server = new server.Server();
        int port = server.run(8080);
        Game.start(port, new Scanner(System.in));
        server.stop();
    }
}