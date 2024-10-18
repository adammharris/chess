package model;

/**
 * Represents a user playing the chess game.
 * @param username Access point for getting UserData
 * @param password Required to obtain a user's AuthData
 * @param email
 */
public record UserData(
        String username,
        String password,
        String email
) { }
