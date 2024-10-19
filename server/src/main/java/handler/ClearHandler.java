package handler;

import spark.Request;
import spark.Response;

public class ClearHandler {
    public Object clear(Request request, Response response) {
        request.headers("Accept");
        // TODO: delete everything
        return "{}";
    }
}
