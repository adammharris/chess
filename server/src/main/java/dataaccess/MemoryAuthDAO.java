package dataaccess;

import model.AuthData;

import java.util.HashMap;
import java.util.UUID;

public class MemoryAuthDAO implements AuthDAO {
    private static MemoryAuthDAO instance;
    private HashMap<String, AuthData> auths = new HashMap<>();
    private MemoryAuthDAO() {}

    public static MemoryAuthDAO getInstance() {
        if (instance == null) {
            instance = new MemoryAuthDAO();
        }
        return instance;
    }

    @Override
    public AuthData createAuth(String username) throws DataAccessException {
        if (auths.get(username) != null) throw new DataAccessException("Already exists, cannot create");
        AuthData newAuth = new AuthData(UUID.randomUUID().toString(), username);
        AuthData previousAuth = auths.put(newAuth.username(), newAuth);
        return newAuth;
    }

    @Override
    public AuthData getAuth(String username) throws DataAccessException {
        AuthData auth = auths.get(username);
        if (auth == null) throw new DataAccessException("username not found, cannot get");
        return auth;
    }
    //public AuthData getAuth(Str)

    @Override
    public void deleteAuth(String authToken) throws DataAccessException {
        String username = getUsername(authToken);
        AuthData auth = new AuthData(authToken, username);
        boolean wasRemoved = auths.remove(auth.username(), auth);
        if (!wasRemoved) throw new DataAccessException("Deletion failed");
    }

    @Override
    public void clear() {
        auths.clear();
    }

    public String getUsername(String authToken) throws DataAccessException {
        for (AuthData auth : auths.values()) {
            if (auth.authToken().equals(authToken)) return auth.username();
        }
        throw new DataAccessException("authToken not found");
    }
}
