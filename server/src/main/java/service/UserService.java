package service;

import dataaccess.DataAccess;
import dataaccess.DataAccessException;
import datamodel.AuthData;
import datamodel.UserData;
import org.mindrot.jbcrypt.BCrypt;

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
        String hashedPassword = BCrypt.hashpw(user.password(), BCrypt.gensalt());
        dataAccess.createUser(new UserData(user.username(), hashedPassword, user.email()));
        var authData = new AuthData(generateAuthToken(), user.username());
        dataAccess.createAuth(authData);
        return authData;
    }

    public AuthData login(UserData user) {
        var storedUserData = dataAccess.getUser(user.username());
        if (storedUserData == null) {
            throw new UnauthorizedException("User does not exist");
        }
        if (!BCrypt.checkpw(user.password(), storedUserData.password())) {
            throw new UnauthorizedException("Password does not match");
        }
        var authData = new AuthData(generateAuthToken(), user.username());
        dataAccess.createAuth(authData);
        return authData;
    }

    public void logout(String authToken) {
        if (dataAccess.getAuth(authToken) == null) {
            throw new UnauthorizedException("Not a recognized auth token");
        }
        dataAccess.deleteAuth(authToken);

    }

    public void clear() {
        dataAccess.clear();
    }

    private String generateAuthToken() {
        return UUID.randomUUID().toString();
    }
}
