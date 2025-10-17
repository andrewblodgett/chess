package dataaccess;

import datamodel.AuthData;
import datamodel.GameData;
import datamodel.UserData;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;

public class MemoryDataAccess implements DataAccess {

    private final HashSet<AuthData> authDataSet;
    private final HashSet<GameData> gameDataSet;
    private final HashSet<UserData> userDataSet;

    public MemoryDataAccess() {

        this.authDataSet = new HashSet<AuthData>();
        this.gameDataSet = new HashSet<GameData>();
        this.userDataSet = new HashSet<UserData>();
    }

    @Override
    public void clear() {

    }

    @Override
    public void createUser(UserData user) {
        userDataSet.add(user);
    }

    @Override
    public UserData getUser(UserData user) {
        if (userDataSet.contains(user)) {
            return user;

        }
        return null;
    }

    @Override
    public void createGame(GameData game) {

    }

    @Override
    public GameData getGame(int gameID) {
        return null;
    }

    @Override
    public Collection<GameData> listGames() {
        return List.of();
    }

    @Override
    public void updateGame(int gameID) {

    }

    @Override
    public void createAuth(AuthData authData) {

    }

    @Override
    public void deleteAuth() {

    }
}
