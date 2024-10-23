package dataaccess;

import model.AuthData;

import java.util.HashMap;
import java.util.UUID;

public class MemoryAuthDAO implements AuthDAO {
    private static MemoryAuthDAO instance;
    private final HashMap<String, AuthData> auths = new HashMap<>();
    private MemoryAuthDAO() {}

    public static MemoryAuthDAO getInstance() {
        if (instance == null) {
            instance = new MemoryAuthDAO();
        }
        return instance;
    }

    @Override
    public AuthData createAuth(String username) {
        AuthData newAuth = new AuthData(UUID.randomUUID().toString(), username);
        auths.put(newAuth.authToken(), newAuth);
        return newAuth;
    }

    @Override
    public AuthData getAuth(String authToken) throws DataAccessException {
        AuthData auth = auths.get(authToken);
        if (auth == null) {
            throw new DataAccessException("Error: Not found");
        }
        return auth;
    }

    @Override
    public void deleteAuth(String authToken) throws DataAccessException {
        AuthData auth = auths.remove(authToken);
        if (auth == null) {
            throw new DataAccessException("Deletion failed");
        }
    }

    @Override
    public void clear() {
        auths.clear();
    }

    public String getUsername(String authToken) throws DataAccessException {
        AuthData auth = auths.get(authToken);
        if (auth != null) {
            return auth.username();
        }
        throw new DataAccessException("Error: Unauthorized");
    }
}
