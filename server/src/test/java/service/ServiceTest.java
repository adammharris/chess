package service;

import server.Server;

public abstract class ServiceTest {
    protected static Server myServer = new Server();

    public static void startServer() {
        var port = myServer.run(8080);
        System.out.println("Started test HTTP server on " + port);
    }
}
