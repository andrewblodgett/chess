package dataaccess;

import datamodel.AuthData;
import datamodel.GameData;
import datamodel.UserData;
import service.UnauthorizedException;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;

public class MemoryDataAccess implements DataAccess {

    private HashSet<AuthData> authDataSet;
    private HashSet<GameData> gameDataSet;
    private HashSet<UserData> userDataSet;

    public MemoryDataAccess() {

        this.authDataSet = new HashSet<AuthData>();
        this.gameDataSet = new HashSet<GameData>();
        this.userDataSet = new HashSet<UserData>();
    }

    @Override
    public void clear() {
        authDataSet = new HashSet<AuthData>();
        gameDataSet = new HashSet<GameData>();
        userDataSet = new HashSet<UserData>();
    }

    @Override
    public void createUser(UserData user) {
        userDataSet.add(user);
    }

    @Override
    public UserData getUser(String username) {
        for (var u : userDataSet) {
            if (u.username().equals(username)) {
                return u;
            }
        }
        return null;
    }

    @Override
    public void createGame(GameData game) {
        gameDataSet.add(game);
    }

    @Override
    public GameData getGame(int gameID) throws DataAccessException {
        for (var g : gameDataSet) {
            if (g.gameID() == gameID) {
                return g;
            }
        }
        throw new DataAccessException("Game ID is invalid");

    }

    @Override
    public Collection<GameData> listGames() {
        return gameDataSet;
    }

    @Override
    public void updateGame(GameData game) {
        gameDataSet.removeIf(g -> g.gameID() == game.gameID());
        gameDataSet.add(game);
    }

    @Override
    public void createAuth(AuthData authData) {
        authDataSet.add(authData);
    }

    @Override
    public void deleteAuth(String authToken) {
        AuthData authData = null;
        for (var a : authDataSet) {
            if (a.authToken().equals(authToken)) {
                authData = a;
                break;
            }
        }
        if (authData == null) {
            throw new UnauthorizedException("The given authToken is invalid");
        }
        authDataSet.remove(authData);
    }

    public AuthData getAuth(String authToken) {
        for (var a : authDataSet) {
            if (a.authToken().equals(authToken)) {
                return a;
            }
        }
        throw new UnauthorizedException("Authtoken is invalid");
    }
}
