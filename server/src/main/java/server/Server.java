package server;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import dataaccess.DataAccess;
import dataaccess.MemoryDataAccess;
import datamodel.AuthData;
import datamodel.UserData;
import io.javalin.*;
import io.javalin.http.Context;
import service.UnauthorizedException;
import service.UserAlreadyRegisteredException;
import service.UserService;

import java.util.Map;

public class Server {

    private final Javalin server;
    private final DataAccess dataAccess = new MemoryDataAccess();
    private final UserService userService = new UserService(dataAccess);

    private final String ERROR_RESPONSE = "{ \"message\": \"Error: bad request\" }";

    public Server() {
        server = Javalin.create(config -> config.staticFiles.add("web"));
        server.delete("db", this::clear);
        server.post("user", this::register);
        server.post("session", this::login);
        server.delete("session", this::logout);
        server.post("game", this::createGame);
        server.put("game", this::joinGame);
        server.get("game", this::listGames);

    }

    private void clear(Context ctx) {
        userService.clear();
    }

    private void register(Context ctx) {
        var serializer = new Gson();
        var response = "";


        var requestedUser = serializer.fromJson(ctx.body(), UserData.class);
        if (requestedUser.email() == null || requestedUser.password() == null || requestedUser.username() == null) {
            response = ERROR_RESPONSE;
            ctx.status(400);
        } else {
            try {
                var authData = userService.register(requestedUser);
                response = serializer.toJson(authData, AuthData.class);

            } catch (UserAlreadyRegisteredException e) {
                response = "{ \"message\": \"Error: already taken\" }";
                ctx.status(403);
            } catch (Exception e) {
                response = ERROR_RESPONSE;
                ctx.status(400);
            }

        }
        ctx.result(response);

    }

    private void login(Context ctx) {
        var serializer = new Gson();
        var requestedUser = serializer.fromJson(ctx.body(), UserData.class);
        var response = "";
        if (requestedUser.password() == null || requestedUser.username() == null) {
            response = ERROR_RESPONSE;
            ctx.status(400);
        } else {
            try {
                var authData = userService.login(requestedUser);
                response = serializer.toJson(authData, AuthData.class);

            } catch (UnauthorizedException e) {
                response = "{ \"message\": \"Error: unauthorized\" }";
                ctx.status(401);
            } catch (Exception e) {
                response = ERROR_RESPONSE;
                ctx.status(400);
            }

        }
        ctx.result(response);
    }

    private void logout(Context ctx) {
        var serializer = new Gson();
        var authToken = ctx.header("authorization");
//        System.out.println("body:" + ctx.body());

        var response = "";
        if (authToken == null) {
            response = ERROR_RESPONSE;
            ctx.status(400);
        } else {
            try {
                userService.logout(authToken);

            } catch (UnauthorizedException e) {
                response = "{ \"message\": \"Error: unauthorized\" }";
                ctx.status(401);
            } catch (Exception e) {
                response = ERROR_RESPONSE;
                ctx.status(400);
            }

        }
        ctx.result(response);
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
