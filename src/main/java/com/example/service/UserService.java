package com.example.service;

import com.example.model.ApprovalStatus;
import com.example.model.Role;
import com.example.model.User;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/** Static "database" of users. Cannot be instantiated. */
public final class UserService {

    private static final Map<String, User> USERS = new LinkedHashMap<>();

    private UserService() { }

    private static String key(String username) {
        return username == null ? "" : username.trim().toLowerCase();
    }

    public static boolean usernameExists(String username) {
        return USERS.containsKey(key(username));
    }

    // ---------- first run ----------

    public static boolean hasAdmin() {
        return countByRole(Role.ADMIN) > 0;
    }

    /** First-run only: creates the one administrator. Refuses if an administrator already exists. */
    public static User registerFirstAdmin(String fullName, String username, String email,
                                          String password, String confirm) {
        if (hasAdmin()) {
            throw new IllegalArgumentException("An administrator already exists.");
        }
        return createAccount(fullName, username, email, password, confirm,
                Role.ADMIN, ApprovalStatus.APPROVED);
    }

    // ---------- self-service ----------

    /** Public sign-up: students are active at once; teachers wait for administrator approval. */
    public static User register(String fullName, String username, String email,
                                String password, String confirm, Role role) {
        if (role != Role.STUDENT && role != Role.TEACHER) {
            throw new IllegalArgumentException("Please choose Student or Teacher.");
        }
        ApprovalStatus approval = role == Role.TEACHER ? ApprovalStatus.PENDING : ApprovalStatus.APPROVED;
        return createAccount(fullName, username, email, password, confirm, role, approval);
    }

    /** Returns the user if credentials match and the account may log in; otherwise throws. */
    public static User login(String username, String password) {
        User user = USERS.get(key(username));
        if (user == null || !user.checkPassword(password)) {
            throw new IllegalArgumentException("Invalid username or password.");
        }
        if (user.getApproval() == ApprovalStatus.PENDING) {
            throw new IllegalArgumentException(
                    "Your registration is still waiting for administrator approval. Please try logging in later.");
        }
        if (user.getApproval() == ApprovalStatus.REJECTED) {
            throw new IllegalArgumentException(
                    "Your registration was not approved. Please contact the administrator.");
        }
        if (!user.isActive()) {
            throw new IllegalArgumentException("This account is deactivated. Please contact an administrator.");
        }
        return user;
    }

    // ---------- administrator actions ----------

    public static List<User> getAll() { return new ArrayList<>(USERS.values()); }

    public static int count() { return USERS.size(); }

    public static int countByRole(Role role) {
        int total = 0;
        for (User user : USERS.values()) {
            if (user.getRole() == role) {
                total++;
            }
        }
        return total;
    }

    public static int countPending() {
        int total = 0;
        for (User user : USERS.values()) {
            if (user.getApproval() == ApprovalStatus.PENDING) {
                total++;
            }
        }
        return total;
    }

    /** Administrator creates a ready-to-use student or teacher account. */
    public static User create(User actor, String fullName, String username, String email,
                              String password, Role role) {
        requireAdmin(actor);
        requireManageable(role);
        User created = createAccount(fullName, username, email, password, password,
                role, ApprovalStatus.APPROVED);
        ActivityLog.record(actor, "Created " + role + " account @" + created.getUsername());
        return created;
    }

    public static void approve(User actor, User target) {
        requireAdmin(actor);
        target.setApproval(ApprovalStatus.APPROVED);
        ActivityLog.record(actor, "Approved @" + target.getUsername());
    }

    public static void reject(User actor, User target) {
        requireAdmin(actor);
        target.setApproval(ApprovalStatus.REJECTED);
        ActivityLog.record(actor, "Rejected registration of @" + target.getUsername());
    }

    public static void changeRole(User actor, User target, Role role) {
        requireAdmin(actor);
        requireManageable(role);
        requireManageable(target.getRole());
        target.setRole(role);
        ActivityLog.record(actor, "Changed @" + target.getUsername() + " to " + role);
    }

    public static void setActive(User actor, User target, boolean active) {
        requireAdmin(actor);
        requireNotSelf(actor, target, "deactivate your own account");
        target.setActive(active);
        ActivityLog.record(actor, (active ? "Activated @" : "Deactivated @") + target.getUsername());
    }

    public static void resetPassword(User actor, User target, String newPassword) {
        requireAdmin(actor);
        target.resetPassword(newPassword);
        ActivityLog.record(actor, "Reset password for @" + target.getUsername());
    }

    public static void delete(User actor, User target) {
        requireAdmin(actor);
        requireNotSelf(actor, target, "delete your own account");
        USERS.remove(key(target.getUsername()));
        ActivityLog.record(actor, "Deleted account @" + target.getUsername());
    }

    // ---------- private helpers ----------

    private static void requireAdmin(User actor) {
        if (actor == null || !actor.getRole().canManageUsers()) {
            throw new IllegalArgumentException("Only administrators can do that.");
        }
    }

    private static void requireNotSelf(User actor, User target, String what) {
        if (actor == target) {
            throw new IllegalArgumentException("You cannot " + what + ".");
        }
    }

    /** Administrators manage students and teachers only. */
    private static void requireManageable(Role role) {
        if (role == Role.ADMIN) {
            throw new IllegalArgumentException("Administrator accounts cannot be created or changed here.");
        }
    }

    private static User createAccount(String fullName, String username, String email,
                                      String password, String confirm, Role role,
                                      ApprovalStatus approval) {
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
        User user = new User(fullName, username.trim(), email, password, role);
        user.setApproval(approval);
        USERS.put(key(username), user);
        DataStore.save();
        return user;
    }

    // package-private: only DataStore uses these
    static LinkedHashMap<String, User> snapshot() { return new LinkedHashMap<>(USERS); }

    static void restore(Map<String, User> loaded) {
        USERS.clear();
        USERS.putAll(loaded);
        User.restoreCounter(USERS.values());
    }
}
