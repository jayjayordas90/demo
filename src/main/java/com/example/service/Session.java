package com.example.service;

import com.example.model.User;

/** Tracks who is currently using the app (static: there is only one session). */
public final class Session {

    private static User currentUser;

    private Session() { }

    public static void start(User user) { currentUser = user; }

    public static void end() { currentUser = null; }

    public static User getCurrentUser() { return currentUser; }

    public static boolean hasSession() { return currentUser != null; }

    public static boolean isLoggedIn() {
        return currentUser != null && !currentUser.isAnonymous();
    }
}
