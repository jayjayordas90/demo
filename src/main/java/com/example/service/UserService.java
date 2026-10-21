package com.example.service;

import com.example.model.User;

import java.util.LinkedHashMap;
import java.util.Map;

/** Static, in-memory "database" of users. Cannot be instantiated. */
public final class UserService {

    private static final Map<String, User> USERS = new LinkedHashMap<>();

    static {
        // Demo account so you can test the login page immediately.
        USERS.put("demo", new User("Demo User", "demo", "demo@paperkite.com", "demo123"));
    }

    private UserService() { }

    private static String key(String username) {
        return username == null ? "" : username.trim().toLowerCase();
    }

    public static boolean usernameExists(String username) {
        return USERS.containsKey(key(username));
    }

    /** Validates input, creates the account, and returns it. Throws IllegalArgumentException on bad input. */
    public static User register(String fullName, String username, String email,
                                String password, String confirm) {
        if (fullName.isBlank() || username.isBlank() || email.isBlank() || password.isEmpty()) {
            throw new IllegalArgumentException("Please fill in all fields.");
        }
        if (!username.trim().matches("[A-Za-z0-9_]{4,20}")) {
            throw new IllegalArgumentException("Username must be 4-20 letters, numbers or underscores.");
        }
        if (!User.isValidEmail(email)) {
            throw new IllegalArgumentException("Please enter a valid email address.");
        }
        if (password.length() < 6) {
            throw new IllegalArgumentException("Password must be at least 6 characters.");
        }
        if (!password.equals(confirm)) {
            throw new IllegalArgumentException("Passwords do not match.");
        }
        if (usernameExists(username)) {
            throw new IllegalArgumentException("That username is already taken.");
        }
        User user = new User(fullName, username.trim(), email, password);
        USERS.put(key(username), user);
        return user;
    }

    /** Returns the user if credentials match; otherwise throws IllegalArgumentException. */
    public static User login(String username, String password) {
        User user = USERS.get(key(username));
        if (user == null || !user.checkPassword(password)) {
            throw new IllegalArgumentException("Invalid username or password.");
        }
        return user;
    }
}
