package client;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Locale;

public class ServerFacade {

    private static final HttpClient httpClient = HttpClient.newHttpClient();
    private final int port;

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

    public String register(String username, String password, String email) {
        return "";
    }

    public String listGames(String authToken) throws Exception {
        var response = get("game", authToken);
        return response.body();
    }
}
