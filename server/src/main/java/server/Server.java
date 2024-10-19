package server;

import spark.*;
import handler.*;

public class Server {

    public int run(int desiredPort) {
        Spark.port(desiredPort);

        Spark.staticFiles.location("web");

        // Register your endpoints and handle exceptions here.
        Spark.post("/user", new RegisterHandler()::register);
        Spark.post("/session", new LoginHandler()::login);
        Spark.delete("/session", new LoginHandler()::logout);
        Spark.get("/game", new GameHandler()::list);
        Spark.post("/game", new GameHandler()::create);
        Spark.put("/game", new GameHandler()::join);
        Spark.delete("/db", new ClearHandler()::clear);

        //This line initializes the server and can be removed once you have a functioning endpoint 
        Spark.init();

        Spark.awaitInitialization();
        return Spark.port();
    }


    public void stop() {
        Spark.stop();
        Spark.awaitStop();
    }
}
