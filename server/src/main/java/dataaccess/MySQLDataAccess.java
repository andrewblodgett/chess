package dataaccess;

import chess.ChessGame;
import com.google.gson.Gson;
import com.google.gson.Gson.*;
import datamodel.AuthData;
import datamodel.GameData;
import datamodel.UserData;

import java.sql.*;
import java.util.Collection;
import java.util.List;
import java.util.Map;

public class MySQLDataAccess implements DataAccess {


    public static void main(String[] args) {

        MySQLDataAccess m = new MySQLDataAccess();
        m.clear();
        m.configureDatabase();
        m.createUser(new UserData("jimothy", "password123", "123@dfsfds.cm"));
        m.createUser(new UserData("jimmy", "password123", "113456456456@fds.cm"));
        m.createUser(new UserData("tomithoi", "password123", "1@dfsfds.cm"));
        System.out.println(m.getUser("jimmy"));
        m.createGame(new GameData(42, "jim", "john", "sdfsdfsd", new ChessGame()));
        System.out.println(m.getGame(42));
    }

    public MySQLDataAccess() throws DataAccessException {
        configureDatabase();
    }

    private void configureDatabase() throws DataAccessException {
        DatabaseManager.createDatabase();
        String[] statements = {
                """
        CREATE TABLE IF NOT EXISTS  authData (
            authToken VARCHAR(256) NOT NULL,
            username VARCHAR(256) NOT NULL,
            PRIMARY KEY (authToken),
            INDEX (username)
        )
        """,
                """
        CREATE TABLE IF NOT EXISTS  userData (
            username VARCHAR(256) NOT NULL,
            password VARCHAR(256) NOT NULL,
            email VARCHAR(256) NOT NULL,
            PRIMARY KEY (username),
            INDEX (email)
        )
        """,
                """
        CREATE TABLE IF NOT EXISTS  gameData (
            gameID INT NOT NULL AUTO_INCREMENT,
            whiteUsername VARCHAR(256),
            blackUsername VARCHAR(256),
            gameName VARCHAR(256) NOT NULL,
            game TEXT NOT NULL,
            PRIMARY KEY (gameID),
            INDEX (gameName)
        )
        """
        };
        for (int i = 0; i < statements.length; i++) {
            try (var conn = DatabaseManager.getConnection()) {
                var preparedStatement = conn.prepareStatement(statements[i]);
                preparedStatement.executeUpdate();
            } catch (SQLException e) {
                throw new DataAccessException("failed to add table number " + Integer.toString(i));
            }
        }
    }

    @Override
    public void clear() {
        try (var conn = DatabaseManager.getConnection()) {
            var preparedStatement = conn.prepareStatement("DROP DATABASE chess");
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            throw new DataAccessException("failed to remove database");
        }
    }

    @Override
    public void createUser(UserData user) {
        try (var conn = DatabaseManager.getConnection()) {
            var preparedStatement = conn.prepareStatement("INSERT INTO userData VALUES (?, ?, ?)");
            preparedStatement.setString(1, user.username());
            preparedStatement.setString(2, user.password());
            preparedStatement.setString(3, user.email());
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            throw new DataAccessException("failed to add user because " + e.toString());
        }
    }

    @Override
    public UserData getUser(String username) {
        try (var conn = DatabaseManager.getConnection()) {
            var preparedStatement = conn.prepareStatement("SELECT * FROM userData WHERE username=?");
            preparedStatement.setString(1, username);
            try (var result = preparedStatement.executeQuery()) {
                while (result.next()) {
                    return new UserData(result.getString("username"), result.getString("password"), result.getString("email"));
                }
            }

        } catch (SQLException e) {
            throw new DataAccessException("failed to get user because " + e.toString());
        }
        return null;
    }

    @Override
    public void createGame(GameData game) {
        try (var conn = DatabaseManager.getConnection()) {
            var preparedStatement = conn.prepareStatement("INSERT INTO gameData VALUES (?, ?, ?, ?, ?)");
            preparedStatement.setInt(1, game.gameID());
            preparedStatement.setString(2, game.whiteUsername());
            preparedStatement.setString(3, game.blackUsername());
            preparedStatement.setString(4, game.gameName());
            var json = new Gson().toJson(game.game());
            preparedStatement.setString(5, json);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            throw new DataAccessException("failed to add user because " + e.toString());
        }
    }

    @Override
    public GameData getGame(int gameID) throws DataAccessException {
        try (var conn = DatabaseManager.getConnection()) {
            var preparedStatement = conn.prepareStatement("SELECT * FROM gameData WHERE gameID=?");
            preparedStatement.setInt(1, gameID);
            try (var result = preparedStatement.executeQuery()) {
                while (result.next()) {
                    var gameMap = new Gson().fromJson(result.getString("game"), Map.class);
                    return new GameData(result.getInt("gameID"), result.getString("whiteUsername"), result.getString("blackUsername"), result.getString("gameName"), new ChessGame());
                }
            }

        } catch (SQLException e) {
            throw new DataAccessException("failed to get user because " + e.toString());
        }
        return null;
    }

    @Override
    public Collection<GameData> listGames() {
        return List.of();
    }

    @Override
    public void updateGame(GameData game) {

    }

    @Override
    public void createAuth(AuthData authData) {

    }

    @Override
    public void deleteAuth(String authToken) {

    }

    @Override
    public AuthData getAuth(String authToken) {
        return null;
    }
}
