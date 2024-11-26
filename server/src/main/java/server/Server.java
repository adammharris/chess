package server;

import service.ClearHandler;
import service.GameHandler;
import service.LoginHandler;
import service.RegisterHandler;
import spark.*;

public class Server {

    public int run(int desiredPort) {
        Spark.port(desiredPort);

        Spark.staticFiles.location("web");

        Spark.webSocket("/ws", WSServer.class);

        // Register your endpoints and handle exceptions here.
        Spark.post("/user", new RegisterHandler()::register);
        Spark.post("/session", new LoginHandler()::login);
        Spark.delete("/session", new LoginHandler()::logout);
        Spark.get("/game", new GameHandler()::list);
        Spark.post("/game", new GameHandler()::create);
        Spark.put("/game", new GameHandler()::join);
        Spark.delete("/db", new ClearHandler()::clear);
        Spark.get("/echo/:msg", (req, res) -> "HTTP response: " + req.params(":msg"));

        Spark.awaitInitialization();
        return Spark.port();
    }


    public void stop() {
        Spark.stop();
        Spark.awaitStop();
    }
}
