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
    private static final Gson gson = new Gson();
    private static final Server server = new Server();

    public ServerFacade() {
        try {
            server.run(8080);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void stop() {
        server.stop();
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
            url = new URI("http://localhost:%s/%s".formatted(8080, urlPath)).toURL();
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
        if (requestMethod.equals("POST") || requestMethod.equals("PUT")) {
            connection.setDoOutput(true);
        }
        connection.connect();
        return connection;
    }

    private <T> T getRequest(String urlPath, String authToken, Class<T> Response) throws IOException {
        URL url = getURL(urlPath);
        HttpURLConnection connection = getConnection(url, "GET", authToken);
        InputStream responseBody = connection.getInputStream();
        return gson.fromJson(inputStreamToString(responseBody), Response);
    }

    private <S, T> T postRequest(String urlPath, String authToken, S Request, Class<T> Response) throws IOException {
        URL url = getURL(urlPath);
        HttpURLConnection connection = getConnection(url, "POST", authToken);

        // Write Request
        try(OutputStream requestBody = connection.getOutputStream()) {
            String userJson = gson.toJson(Request);
            requestBody.write(userJson.getBytes());
        }

        // Get Response
        if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
            InputStream responseBody = connection.getInputStream();
            return gson.fromJson(inputStreamToString(responseBody), Response);
        }
        else { // Error
            InputStream responseBody = connection.getErrorStream();
            throw new IOException(inputStreamToString(responseBody));
        }
    }

    public GameData[] listGames(String authToken) throws IOException {
        record ListRequest(GameData[] games) {}
        ListRequest games = getRequest("game", authToken, ListRequest.class);
        return games.games();
    }

    public String register(String username, String password, String email) throws IOException {
        record RegisterResponse(String user, String authToken) {}
        RegisterResponse response = postRequest("user", "", new UserData(username, password, email), RegisterResponse.class);
        return response.authToken();
    }

    public String login(String username, String password) throws IOException {
        record LoginResponse(String authToken, String username) {}
        LoginResponse response = postRequest("session", "", new UserData(username, password, ""), LoginResponse.class);
        return response.authToken();
    }

    public int createGame(String authToken, String gameName) throws IOException {
        record CreateResponse(int gameID) {
        }
        record CreateRequest(String gameName) {}
        CreateResponse response = postRequest("game", authToken, new CreateRequest(gameName), CreateResponse.class);
        return response.gameID();
    }

    public void joinGame(String authToken, String playerColor, int gameID) throws IOException {
        URL url = getURL("game");
        HttpURLConnection connection = getConnection(url, "PUT", authToken);

        // Write Request
        try(OutputStream requestBody = connection.getOutputStream()) {
            record JoinRequest(String playerColor, int gameID) {}
            String userJson = gson.toJson(new JoinRequest(playerColor, gameID));
            requestBody.write(userJson.getBytes());
        }
    }

    public void logout(String authToken) throws IOException {
        URL url = getURL("session");
        getConnection(url, "DELETE", authToken);
    }
}