package service;

import dataaccess.DataAccess;
import datamodel.AuthData;
import datamodel.UserData;

import java.util.UUID;

public class UserService {

    private final DataAccess dataAccess;

    public UserService(DataAccess dataAccess) {
        this.dataAccess = dataAccess;
    }

    public AuthData register(UserData user) {
        if (dataAccess.getUser(user.username()) != null) {
            throw new UserAlreadyRegisteredException("Username is already taken");
        }
        dataAccess.createUser(user);
        var authData = new AuthData(generateAuthToken(), user.username());
        dataAccess.createAuth(authData);
        return authData;
    }

    public AuthData login(UserData user) {
        var storedUserData = dataAccess.getUser(user.username());
        if (storedUserData == null) {
            throw new UnauthorizedException("User does not exist");
        }
        if (!storedUserData.password().equals(user.password())) {
            throw new UnauthorizedException("Password does not match");
        }
        var authData = new AuthData(generateAuthToken(), user.username());
        dataAccess.createAuth(authData);
        return authData;
    }

    public void logout(String authToken) {
        dataAccess.deleteAuth(authToken);
    }

    public void clear() {
        dataAccess.clear();
    }

    private String generateAuthToken() {
        return UUID.randomUUID().toString();
    }
}
