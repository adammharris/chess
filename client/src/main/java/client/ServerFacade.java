package client;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import model.GameData;
import model.UserData;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.StandardCharsets;


public class ServerFacade {
    private static int port = 8080;
    private static final Gson gson = new Gson();
    private record JoinRequest(String playerColor, int gameID) {}
    private record Empty() {}
    private record RegisterResponse(String user, String authToken) {}
    private record ListRequest(GameData[] games) {}
    private record CreateResponse(int gameID) { }
    private record CreateRequest(String gameName) {}
    private record Messenger(String message) {}

    public ServerFacade(int portNumber) {
        port = portNumber;
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

    private URL getURL(String urlPath) throws IOException {
        URL url;
        try {
            url = new URI("http://localhost:%s/%s".formatted(port, urlPath)).toURL();
        } catch (URISyntaxException e) {
            throw new IOException(e);
        }
        return url;
    }

    private HttpURLConnection getConnection(URL url, String requestMethod, String authToken) throws IOException {
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setReadTimeout(5000);
        connection.setRequestMethod(requestMethod);
        if (!authToken.isEmpty()) {
            connection.addRequestProperty("Authorization", authToken);
        }
        if (requestMethod.equals("POST") || requestMethod.equals("PUT") || requestMethod.equals("DELETE")) {
            connection.setDoOutput(true);
        }
        connection.connect();
        return connection;
    }

    private <S, T> T postRequest(String urlPath, String authToken, S Request, Class<T> Response) throws IOException {
        return postRequest(urlPath, authToken, Request, Response, "POST");
    }
    private <S, T> T postRequest(String urlPath, String authToken, S Request, Class<T> Response, String requestMethod) throws IOException {
        URL url = getURL(urlPath);
        HttpURLConnection connection = getConnection(url, requestMethod, authToken);

        // Write Request
        try(OutputStream requestBody = connection.getOutputStream()) {
            String userJson = gson.toJson(Request);
            requestBody.write(userJson.getBytes());
        }

        // Get Response
        if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
            InputStream responseBody = connection.getInputStream();
            return gson.fromJson(inputStreamToString(responseBody), Response);
        } else { // Error
            InputStream responseBody = connection.getErrorStream();
            String message = inputStreamToString(responseBody);
            try {
                Messenger messenger = gson.fromJson(message, Messenger.class);
                message = messenger.message();
            } catch (JsonSyntaxException e) {
                message = message + " and " + e.getMessage();
            }
            throw new IOException(message);
        }
    }

    public GameData[] listGames(String authToken) throws IOException {
        URL url = getURL("game");
        HttpURLConnection connection = getConnection(url, "GET", authToken);
        InputStream responseBody = connection.getInputStream();
        ListRequest games = gson.fromJson(inputStreamToString(responseBody), ListRequest.class);
        //ListRequest games = getRequest("game", authToken, ListRequest.class);
        return games.games();
    }

    public String register(String username, String password, String email) throws IOException {
        RegisterResponse response = postRequest("user", "", new UserData(username, password, email), RegisterResponse.class);
        return response.authToken();
    }

    public String login(String username, String password) throws IOException {
        record LoginResponse(String authToken, String username) {}
        LoginResponse response = postRequest("session", "", new UserData(username, password, ""), LoginResponse.class);
        return response.authToken();
    }

    public int createGame(String authToken, String gameName) throws IOException {
        CreateResponse response = postRequest("game", authToken, new CreateRequest(gameName), CreateResponse.class);
        return response.gameID();
    }

    public void joinGame(String authToken, String playerColor, int gameID) throws IOException {
        postRequest("game", authToken, new JoinRequest(playerColor, gameID), Empty.class, "PUT");
    }

    public void logout(String authToken) throws IOException {
        postRequest("session", authToken, new Empty(), Empty.class, "DELETE");
    }

    public void clear() throws IOException {
        postRequest("db", "", new Empty(), Empty.class, "DELETE");
    }
}