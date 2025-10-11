package server;

import com.google.gson.Gson;
import io.javalin.*;
import io.javalin.http.Context;

import java.util.Map;

public class Server {

    private final Javalin server;

    public Server() {
        server = Javalin.create(config -> config.staticFiles.add("web"));
        server.delete("db", ctx -> ctx.result("{}"));
        server.post("user", this::register);
        server.post("session", this::login);
        server.delete("session", this::logout);
        server.post("game", this::createGame);
        server.put("game", this::joinGame);
        server.get("game", this::listGames);
        // Register your endpoints and exception handlers here.

    }

    private void register(Context ctx) {
        var serializer = new Gson();

        var request = serializer.fromJson(ctx.body(), Map.class);
        request.put("authToken", "yeet");
        var response = serializer.toJson(request);
        ctx.result(response);
    }

    private void login(Context ctx) {
        var serializer = new Gson();
        var request = serializer.fromJson(ctx.body(), Map.class);
        request.put("authToken", "yeet");
        var response = serializer.toJson(request);
        ctx.result(response);
    }

    private void logout(Context ctx){
        var serializer = new Gson();
//        var request = serializer.fromJson(ctx.body(), Map.class);
//        request.put("authToken", "yeet");
//        var response = serializer.toJson();
        ctx.result("{\"authToken\":\"yeet\"}");
    }

    private void createGame(Context ctx) {
        ctx.result("{\"gameID\":1234}");
    }

    private void joinGame(Context ctx) {
        ctx.result("{}");
    }

    private void listGames(Context ctx) {
        ctx.result("{\"games\":[]}");
    }

    public int run(int desiredPort) {
        server.start(desiredPort);
        return server.port();
    }

    public void stop() {
        server.stop();
    }
}
