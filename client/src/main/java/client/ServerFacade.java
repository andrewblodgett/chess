package client;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Locale;
import java.util.Map;

public class ServerFacade {

    private static final HttpClient httpClient = HttpClient.newHttpClient();
    private final int port;
    private final static Gson deserializer = new Gson();


    public ServerFacade(int port) {
        this.port = port;
    }

    public HttpResponse<String> get(String path, String authToken) throws Exception {
        String urlString = String.format(Locale.getDefault(), "http://localhost:%d/%s", port, path);

        var request = HttpRequest.newBuilder().uri(new URI(urlString))
                .GET().header("authorization", authToken)
                .build();
        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() >= 200 && response.statusCode() < 400) {
        } else {
            throw new Exception("Request returned a status code of " + response.statusCode());
        }
        return response;
    }

    public HttpResponse<String> post(String path, String authToken, String body) throws Exception {
        String urlString = String.format(Locale.getDefault(), "http://localhost:%d/%s", port, path);

        var request = HttpRequest.newBuilder().uri(new URI(urlString))
                .POST(HttpRequest.BodyPublishers.ofString(body))
                .header("authorization", authToken)
                .build();
        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() >= 200 && response.statusCode() < 400) {
        } else {
            throw new Exception("Request returned a status code of " + response.statusCode());
        }
        return response;
    }

    public void clear() throws Exception {
        String urlString = String.format(Locale.getDefault(), "http://localhost:%d/db", port);
        var request = HttpRequest.newBuilder().uri(new URI(urlString))
                .DELETE()
                .build();
        httpClient.send(request, HttpResponse.BodyHandlers.ofString());
    }

    public String register(String username, String password, String email) throws Exception {
        var response = post("user", "", String.format("{ \"username\":\"%s\", \"password\":\"%s\", \"email\":\"%s\" }", username, password, email));
        var mapped = deserializer.fromJson(response.body(), Map.class);
        return mapped.get("authToken").toString();
    }

    public String login(String username, String password) {
        return "";
    }

    public String listGames(String authToken) throws Exception {
        var response = get("game", authToken);
        return response.body();
    }
}
