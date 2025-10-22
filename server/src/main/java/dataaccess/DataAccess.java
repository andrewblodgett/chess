package dataaccess;

import datamodel.AuthData;
import datamodel.GameData;
import datamodel.UserData;

import java.util.Collection;

public interface DataAccess {
    void clear();

    void createUser(UserData user);

    UserData getUser(String username);

    void createGame(GameData game);

    GameData getGame(int gameID) throws DataAccessException;

    Collection<GameData> listGames();

    void updateGame(GameData game);

    void createAuth(AuthData authData);

    void deleteAuth(String authToken);

    AuthData getAuth(String authToken);
}
