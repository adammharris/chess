package client;

import com.google.gson.Gson;
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

    private void doGet(String urlString) throws IOException {
        URL url = new URL(urlString);

        HttpURLConnection connection = (HttpURLConnection) url.openConnection();

        connection.setReadTimeout(5000);
        connection.setRequestMethod("GET");

        // Set HTTP request headers, if necessary
        // connection.addRequestProperty("Accept", "text/html");
        // connection.addRequestProperty("Authorization", "fjaklc8sdfjklakl");

        connection.connect();

        if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
            // Get HTTP response headers, if necessary
            // Map<String, List<String>> headers = connection.getHeaderFields();

            // OR

            //connection.getHeaderField("Content-Length");

            InputStream responseBody = connection.getInputStream();
            // Read and process response body from InputStream ...
        } else {
            // SERVER RETURNED AN HTTP ERROR

            InputStream responseBody = connection.getErrorStream();
            // Read and process error response body from InputStream ...
        }
    }

    public void register(String username, String password, String email) throws IOException {
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
        }
        else { // Error
            InputStream responseBody = connection.getErrorStream();
        }
    }
}