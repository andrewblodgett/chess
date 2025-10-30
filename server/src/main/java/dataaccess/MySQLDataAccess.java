package dataaccess;

import chess.ChessGame;
import com.google.gson.Gson;
import com.google.gson.Gson.*;
import datamodel.AuthData;
import datamodel.GameData;
import datamodel.UserData;
import service.UnauthorizedException;

import java.io.*;
import java.sql.*;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

public class MySQLDataAccess implements DataAccess {

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
            gameID INT NOT NULL,
            whiteUsername VARCHAR(256),
            blackUsername VARCHAR(256),
            gameName VARCHAR(256) NOT NULL,
            game BLOB NOT NULL,
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
            var preparedStatement = conn.prepareStatement("DELETE FROM userData");
            preparedStatement.executeUpdate();
            preparedStatement = conn.prepareStatement("DELETE FROM authData");
            preparedStatement.executeUpdate();
            preparedStatement = conn.prepareStatement("DELETE FROM gameData");
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
            var byteOutputStream = new ByteArrayOutputStream();
            var objectOutputStream = new ObjectOutputStream(byteOutputStream);
            objectOutputStream.writeObject(game.game());
            preparedStatement.setBytes(5, byteOutputStream.toByteArray());
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            throw new DataAccessException("failed to add user because " + e.toString());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public GameData getGame(int gameID) throws DataAccessException {
        try (var conn = DatabaseManager.getConnection()) {
            var preparedStatement = conn.prepareStatement("SELECT * FROM gameData WHERE gameID=?");
            preparedStatement.setInt(1, gameID);
            try (var result = preparedStatement.executeQuery()) {
                while (result.next()) {
                    ByteArrayInputStream bais = new ByteArrayInputStream(result.getBytes("game"));
                    ObjectInputStream ois = new ObjectInputStream(bais);
                    ChessGame loadedGame = (ChessGame) ois.readObject();
                    return new GameData(result.getInt("gameID"), result.getString("whiteUsername"), result.getString("blackUsername"),
                            result.getString("gameName"), loadedGame);
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            } catch (ClassNotFoundException e) {
                throw new RuntimeException(e);
            }

        } catch (SQLException e) {
            throw new DataAccessException("failed to get user because " + e.toString());
        }
        return null;
    }

    @Override
    public Collection<GameData> listGames() {
        try (var conn = DatabaseManager.getConnection()) {
            var preparedStatement = conn.prepareStatement("SELECT * FROM gameData");
            try (var result = preparedStatement.executeQuery()) {
                var allGames = new HashSet<GameData>();
                while (result.next()) {
                    ByteArrayInputStream bais = new ByteArrayInputStream(result.getBytes("game"));
                    ObjectInputStream ois = new ObjectInputStream(bais);
                    ChessGame loadedGame = (ChessGame) ois.readObject();
                    allGames.add(new GameData(result.getInt("gameID"), result.getString("whiteUsername"),
                            result.getString("blackUsername"), result.getString("gameName"), loadedGame));
                }
                return allGames;
            } catch (ClassNotFoundException e) {
                throw new RuntimeException(e);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

        } catch (SQLException e) {
            throw new DataAccessException("failed to get user because " + e.toString());
        }
    }

    @Override
    public void updateGame(GameData game) {
        try (var conn = DatabaseManager.getConnection()) {
            var preparedStatement = conn.prepareStatement("UPDATE gameData SET whiteUsername=?, blackUsername=?, gameName=?, game=? WHERE gameID=?");
            preparedStatement.setString(1, game.whiteUsername());
            preparedStatement.setString(2, game.blackUsername());
            preparedStatement.setString(3, game.gameName());
            var byteOutputStream = new ByteArrayOutputStream();
            var objectOutputStream = new ObjectOutputStream(byteOutputStream);
            objectOutputStream.writeObject(game.game());
            preparedStatement.setBytes(4, byteOutputStream.toByteArray());
            preparedStatement.setInt(5, game.gameID());

            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            throw new DataAccessException("failed to add user because " + e.toString());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void createAuth(AuthData authData) {
        try (var conn = DatabaseManager.getConnection()) {
            var preparedStatement = conn.prepareStatement("INSERT INTO authData VALUES (?, ?)");
            preparedStatement.setString(1, authData.authToken());
            preparedStatement.setString(2, authData.username());
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            throw new DataAccessException("failed to add auth because " + e.toString());
        }
    }

    @Override
    public void deleteAuth(String authToken) {
        try (var conn = DatabaseManager.getConnection()) {
            try (var preparedStatement = conn.prepareStatement("DELETE FROM authData WHERE authToken=?")) {
                preparedStatement.setString(1, authToken);
                preparedStatement.executeUpdate();
            }
        } catch (SQLException e) {
            throw new DataAccessException("failed to delete auth because " + e.toString());
        }

    }

    @Override
    public AuthData getAuth(String authToken) {
        try (var conn = DatabaseManager.getConnection()) {
            var preparedStatement = conn.prepareStatement("SELECT * FROM authData WHERE authToken=?");
            preparedStatement.setString(1, authToken);
            try (var result = preparedStatement.executeQuery()) {
                while (result.next()) {
                    return new AuthData(result.getString("authToken"), result.getString("username"));
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException("failed to get auth because " + e.toString());
        }
        return null;
    }
}
