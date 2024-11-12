package client;

import com.google.gson.Gson;
import model.GameData;
import model.UserData;
import server.Server;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.StandardCharsets;


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

    private String inputStreamToString(InputStream is) throws IOException {
        ByteArrayOutputStream result = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        for (int length; (length = is.read(buffer)) != -1; ) {
            result.write(buffer, 0, length);
        }
        // StandardCharsets.UTF_8.name() > JDK 7
        return result.toString(StandardCharsets.UTF_8);
    }
    public GameData[] listGames(String authToken) throws IOException {
        URL url = null;
        try {
            url = new URI("http://localhost:8080/game").toURL();
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setReadTimeout(5000);
        connection.setRequestMethod("GET");
        connection.addRequestProperty("Authorization", authToken);
        connection.connect();
        if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
            InputStream responseBody = connection.getInputStream();
            record GamesList(GameData[] games) {}
            GamesList gamesList = gson.fromJson(inputStreamToString(responseBody), GamesList.class);
            return gamesList.games();
        } else {
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
            record LoginResponse(String authToken, String username) {}
            LoginResponse fromJson = gson.fromJson(inputStreamToString(responseBody), LoginResponse.class);
            return fromJson.authToken();
        }
        else { // Error
            InputStream responseBody = connection.getErrorStream();
            throw new IOException(responseBody.toString());
        }
    }

    public int createGame(String authToken, String gameName) throws IOException {
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
        connection.addRequestProperty("Authorization", authToken);
        connection.connect();
        try(OutputStream requestBody = connection.getOutputStream()) {
            String json = "{\"gameName\":\"%s\"}".formatted(gameName);
            requestBody.write(json.getBytes());
        }

        if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
            InputStream responseBody = connection.getInputStream();
            System.out.println(inputStreamToString(responseBody));
            record CreateResponse(int gameID) {}
            CreateResponse createResponse = gson.fromJson(responseBody.toString(), CreateResponse.class);
            return createResponse.gameID();
        }
        else { // Error
            InputStream responseBody = connection.getErrorStream();
            throw new IOException(responseBody.toString());
        }
    }
}