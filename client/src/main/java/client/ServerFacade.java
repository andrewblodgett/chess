package client;

import chess.ChessGame;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.ToNumberPolicy;
import com.google.gson.internal.LinkedTreeMap;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.*;

public class ServerFacade {

    private static final HttpClient HTTP_CLIENT = HttpClient.newHttpClient();
    private final int port;
    private final static Gson DESERIALIZER = new GsonBuilder()
            .setObjectToNumberStrategy(ToNumberPolicy.LONG_OR_DOUBLE)
            .create();

    public ServerFacade(int port) {
        this.port = port;
    }

    private HttpResponse<String> get(String path, String authToken) throws Exception {
        String urlString = String.format(Locale.getDefault(), "http://localhost:%d/%s", port, path);

        var request = HttpRequest.newBuilder().uri(new URI(urlString))
                .GET().header("authorization", authToken)
                .build();
        HttpResponse<String> response = HTTP_CLIENT.send(request, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() >= 200 && response.statusCode() < 400) {
        } else {
            throw new Exception("Request returned a status code of " + response.statusCode());
        }
        return response;
    }

    private HttpResponse<String> post(String path, String authToken, String body) throws Exception {
        String urlString = String.format(Locale.getDefault(), "http://localhost:%d/%s", port, path);

        var request = HttpRequest.newBuilder().uri(new URI(urlString))
                .POST(HttpRequest.BodyPublishers.ofString(body))
                .header("authorization", authToken)
                .build();
        HttpResponse<String> response = HTTP_CLIENT.send(request, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() >= 200 && response.statusCode() < 400) {
        } else {
            throw new Exception("Request returned a status code of " + response.statusCode());
        }
        return response;
    }

    private HttpResponse<String> put(String path, String authToken, String body) throws Exception {
        String urlString = String.format(Locale.getDefault(), "http://localhost:%d/%s", port, path);

        var request = HttpRequest.newBuilder().uri(new URI(urlString))
                .PUT(HttpRequest.BodyPublishers.ofString(body))
                .header("authorization", authToken)
                .build();
        HttpResponse<String> response = HTTP_CLIENT.send(request, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() >= 200 && response.statusCode() < 400) {
        } else {
            throw new Exception("Request returned a status code of " + response.statusCode());
        }
        return response;
    }

    private void delete(String path, String authToken) throws Exception {
        String urlString = String.format(Locale.getDefault(), "http://localhost:%d/%s", port, path);
        var request = HttpRequest.newBuilder().uri(new URI(urlString))
                .header("authorization", authToken)
                .DELETE()
                .build();
        var response = HTTP_CLIENT.send(request, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() >= 200 && response.statusCode() < 400) {
        } else {
            throw new Exception("Request returned a status code of " + response.statusCode());
        }
    }

    public void clear() throws Exception {
        delete("db", "");
    }

    public String register(String username, String password, String email) throws Exception {
        var response = post("user", "", String.format("{ \"username\":\"%s\", \"password\":\"%s\", \"email\":\"%s\" }", username, password, email));
        var mapped = DESERIALIZER.fromJson(response.body(), Map.class);
        return mapped.get("authToken").toString();
    }

    public String login(String username, String password) throws Exception {
        var response = post("session", "", String.format("{ \"username\":\"%s\", \"password\":\"%s\"}", username, password));
        var mapped = DESERIALIZER.fromJson(response.body(), Map.class);
        return mapped.get("authToken").toString();
    }

    public void logout(String authToken) throws Exception {
        delete("session", authToken);
    }

    public Long createGame(String authToken, String gameName) throws Exception {
        var response = post("game", authToken, String.format("{ \"gameName\":\"%s\" }", gameName));
        var mapped = DESERIALIZER.fromJson(response.body(), Map.class);
        return (Long) mapped.get("gameID");

    }

    public String listGames(String authToken) throws Exception {
        var response = get("game", authToken);
        var mapped = DESERIALIZER.fromJson(response.body(), Map.class);
        String output = "Current games:";
        var listOfGames = (ArrayList<LinkedTreeMap>) mapped.get("games");
        listOfGames.sort(Comparator.comparing(map -> (Integer) map.get("gameID")));
        for (var game : listOfGames) {
            output += "\nGame number " + game.get("gameID") + ": " + game.get("gameName");
            output += "W: " + game.get("whiteUsername") + " B: " + game.get("blackUsername");
        }
        return output;
    }

    public void joinGame(String authToken, Long gameID, ChessGame.TeamColor playerColor) throws Exception {
        var response = put("game", authToken, String.format("{ \"playerColor\":\"%s\", \"gameID\":\"%d\"}",
                playerColor == ChessGame.TeamColor.WHITE ? "WHITE" : "BLACK", gameID));
    }
}
