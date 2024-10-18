package model;

/**
 * Record of secure information of chess player
 * @param authToken Used to verify player
 * @param username Used to identify player (see UserData.username())
 */
public record AuthData(
        String authToken,
        String username
) { }