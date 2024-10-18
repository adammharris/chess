package server;

import spark.*;
import handler.*;

public class Server {

    public int run(int desiredPort) {
        Spark.port(desiredPort);

        Spark.staticFiles.location("web");

        // Register your endpoints and handle exceptions here.
        //CreateGameHandler cgh = CreateGameHandler();
        Spark.post("/user", this::registerUser);
        //This line initializes the server and can be removed once you have a functioning endpoint 
        Spark.init();

        Spark.awaitInitialization();
        return Spark.port();
    }


    public void stop() {
        Spark.stop();
        Spark.awaitStop();
    }

    private Object registerUser(Request request, Response response) {
        RegisterHandler rg = new RegisterHandler();
        return rg.simpleResponse(request, response);
    }
}
