package dataaccess;

/*
clear: A method for clearing all data from the database. This is used during testing.
createUser: Create a new user.
getUser: Retrieve a user with the given username.
createGame: Create a new game.
getGame: Retrieve a specified game with the given game ID.
listGames: Retrieve all games.
updateGame: Updates a chess game. It should replace the chess game string corresponding to a given gameID. This is used when players join a game or when a move is made.
createAuth: Create a new authorization.
getAuth: Retrieve an authorization given an authToken.
deleteAuth: Delete an authorization so that it is no longer valid
 */


import datamodel.AuthData;
import datamodel.GameData;
import datamodel.UserData;

import java.util.Collection;

public interface DataAccess {
    void clear();

    void createUser(UserData user);

    UserData getUser(UserData user);

    void createGame(GameData game);

    GameData getGame(int gameID);

    Collection<GameData> listGames();

    void updateGame(int gameID);

    void createAuth(AuthData authData);

    void deleteAuth();
}
