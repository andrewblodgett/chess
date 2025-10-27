package dataaccess;

import datamodel.AuthData;
import datamodel.GameData;
import datamodel.UserData;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Collection;
import java.util.List;

public class MySQLDataAccess implements DataAccess {


    public static void main(String[] args) {
        new MySQLDataAccess();
    }

    public MySQLDataAccess() throws DataAccessException {
        configureDatabase();
    }

    private void configureDatabase() throws DataAccessException {
        DatabaseManager.createDatabase();
        String[] statements = {
                """
        CREATE TABLE IF NOT EXISTS  authData (
            `authToken` VARCHAR(256) NOT NULL,
            `username` VARCHAR(256) NOT NULL,
            PRIMARY KEY (`authToken`),
            INDEX (username)
        )
        """,
                """
        CREATE TABLE IF NOT EXISTS  userData (
            `id` INT NOT NULL AUTO_INCREMENT,
            `email` VARCHAR(256) NOT NULL,
            `username` VARCHAR(256) NOT NULL,
            `password` VARCHAR(256) NOT NULL,
            PRIMARY KEY (`id`),
            INDEX (username),
            INDEX (email)
        )
        """,
                """
        CREATE TABLE IF NOT EXISTS  gameData (
            `id` INT NOT NULL AUTO_INCREMENT,
            `whiteUsername` VARCHAR(256),
            `blackUsername` VARCHAR(256),
            `gameName` VARCHAR(256) NOT NULL,
            `game` BLOB NOT NULL,
            PRIMARY KEY (`id`),
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

    }

    @Override
    public void createUser(UserData user) {

    }

    @Override
    public UserData getUser(String username) {
        return null;
    }

    @Override
    public void createGame(GameData game) {

    }

    @Override
    public GameData getGame(int gameID) throws DataAccessException {
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
