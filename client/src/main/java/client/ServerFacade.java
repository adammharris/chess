package client;

import com.google.gson.Gson;
import model.GameData;
import model.UserData;
import server.Server;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;


public class ServerFacade {
    private final Server server = new Server();
    static Gson gson = new Gson();

    public ServerFacade() {
        try {
            server.run(8080);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public GameData[] listGames(String authToken) throws IOException {
        URL url = new URL("http://localhost:8080/game");
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setReadTimeout(5000);
        connection.setRequestMethod("GET");
        // Set HTTP request headers, if necessary
        // connection.addRequestProperty("Accept", "text/html");
        connection.addRequestProperty("Authorization", authToken);
        connection.connect();
        if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
            // Get HTTP response headers, if necessary
            // Map<String, List<String>> headers = connection.getHeaderFields();

            // OR

            //connection.getHeaderField("Content-Length");

            InputStream responseBody = connection.getInputStream();
            return gson.fromJson(responseBody.toString(), GameData[].class);
        } else {
            // SERVER RETURNED AN HTTP ERROR

            InputStream responseBody = connection.getErrorStream();
            // Read and process error response body from InputStream ...
        }
        return new GameData[0];
    }

    public String register(String username, String password, String email) throws IOException {
        URL url = null;
        try {
            url = new URI("http://localhost:%s/user".formatted(8080)).toURL();
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setReadTimeout(5000);
        connection.setRequestMethod("POST");
        connection.setDoOutput(true);
        // Set HTTP request headers, if necessary
        // connection.addRequestProperty("Accept", "text/html");
        connection.connect();
        try(OutputStream requestBody = connection.getOutputStream()) {
            UserData newUser = new UserData(username, password, email);
            String userJson = gson.toJson(newUser);
            requestBody.write(userJson.getBytes());
        }
        if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
            InputStream responseBody = connection.getInputStream();
            return responseBody.toString();
        }
        else { // Error
            InputStream responseBody = connection.getErrorStream();
            throw new IOException(responseBody.toString());
        }
    }

    public String login(String username, String password) throws IOException {
        URL url = null;
        try {
            url = new URI("http://localhost:%s/session".formatted(8080)).toURL();
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setReadTimeout(5000);
        connection.setRequestMethod("POST");
        connection.setDoOutput(true);
        // Set HTTP request headers, if necessary
        // connection.addRequestProperty("Accept", "text/html");
        connection.connect();
        try(OutputStream requestBody = connection.getOutputStream()) {
            UserData loginRequest = new UserData(username, password, null);
            String loginJson = gson.toJson(loginRequest);
            requestBody.write(loginJson.getBytes());
        }
        if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
            InputStream responseBody = connection.getInputStream();
            return responseBody.toString();
        }
        else { // Error
            InputStream responseBody = connection.getErrorStream();
            throw new IOException(responseBody.toString());
        }
    }

    public int createGame(String gameName) throws IOException {
        URL url = null;
        try {
            url = new URI("http://localhost:%s/game".formatted(8080)).toURL();
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setReadTimeout(5000);
        connection.setRequestMethod("POST");
        connection.setDoOutput(true);
        // Set HTTP request headers, if necessary
        // connection.addRequestProperty("Accept", "text/html");
        connection.connect();
        try(OutputStream requestBody = connection.getOutputStream()) {
            String json = "{\"gameName\":\"%s\"}".formatted(gameName);
            requestBody.write(json.getBytes());
        }

        if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
            InputStream responseBody = connection.getInputStream();
            record CreateResponse(int gameID) {};
            CreateResponse createResponse = gson.fromJson(responseBody.toString(), CreateResponse.class);
            return createResponse.gameID();
        }
        else { // Error
            InputStream responseBody = connection.getErrorStream();
            throw new IOException(responseBody.toString());
        }
    }
}