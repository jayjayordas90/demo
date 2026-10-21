package com.example.model;

import java.io.Serializable;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Base64;
import java.util.Collection;
import java.util.regex.Pattern;

/**
 * A PaperKite account holder (or the anonymous guest).
 * Attributes are private; the outside world uses getters/setters (encapsulation).
 */
public class User implements Serializable {

    private static final long serialVersionUID = 1L;

    // ---- static members: shared by ALL User instances ----
    private static final Pattern EMAIL = Pattern.compile("^[\\w.+-]+@[\\w-]+(\\.[\\w-]+)+$");
    private static final SecureRandom RANDOM = new SecureRandom();
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("MMM d, yyyy");
    private static int totalUsers = 0;

    // ---- instance attributes: each object has its own copy ----
    private final int id;                 // final: can never change after construction
    private final String username;
    private final LocalDate memberSince;
    private final String salt;
    private String fullName;
    private String email;
    private String passwordHash;
    private Role role;
    private boolean active = true;
    private ApprovalStatus approval = ApprovalStatus.APPROVED;

    /** Creates a regular student account. */
    public User(String fullName, String username, String email, String password) {
        this(fullName, username, email, password, Role.STUDENT);
    }

    /** Main constructor: creates a registered user with a specific role. */
    public User(String fullName, String username, String email, String password, Role role) {
        this.id = ++totalUsers;
        this.role = role;
        this.username = username;
        this.memberSince = LocalDate.now();
        this.salt = newSalt();
        setFullName(fullName);
        setEmail(email);
        this.passwordHash = hash(password, salt);
    }

    /** Private constructor: only User.anonymous() may use it. */
    private User() {
        this.id = 0;
        this.username = "anonymous";
        this.memberSince = LocalDate.now();
        this.salt = "";
        this.fullName = "Anonymous";
        this.email = "";
        this.passwordHash = "";
        this.role = Role.STUDENT;
    }

    /** Static factory method for the guest user. */
    public static User anonymous() {
        return new User();
    }

    // ---- static helpers ----
    public static int getTotalUsers() {
        return totalUsers;
    }

    /** After loading saved users, continue numbering from the highest existing id. */
    public static void restoreCounter(Collection<User> loaded) {
        int max = 0;
        for (User user : loaded) {
            max = Math.max(max, user.id);
        }
        totalUsers = max;
    }

    public static boolean isValidEmail(String email) {
        return email != null && EMAIL.matcher(email.trim()).matches();
    }

    private static String newSalt() {
        byte[] bytes = new byte[16];
        RANDOM.nextBytes(bytes);
        return Base64.getEncoder().encodeToString(bytes);
    }

    private static String hash(String password, String salt) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] result = digest.digest((salt + password).getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(result);
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("SHA-256 not available", e);
        }
    }

    // ---- instance methods ----
    public boolean isAnonymous() {
        return id == 0;
    }

    public boolean checkPassword(String attempt) {
        if (isAnonymous() || attempt == null) {
            return false;
        }
        return MessageDigest.isEqual(
                passwordHash.getBytes(StandardCharsets.UTF_8),
                hash(attempt, salt).getBytes(StandardCharsets.UTF_8));
    }

    public void changePassword(String current, String newPassword) {
        if (!checkPassword(current)) {
            throw new IllegalArgumentException("Current password is incorrect.");
        }
        if (newPassword == null || newPassword.length() < 6) {
            throw new IllegalArgumentException("New password must be at least 6 characters.");
        }
        this.passwordHash = hash(newPassword, salt);
    }

    public boolean isStaff() {
        return role != Role.STUDENT;
    }

    /** Admin reset: no current password needed. */
    public void resetPassword(String newPassword) {
        if (newPassword == null || newPassword.length() < 6) {
            throw new IllegalArgumentException("Password must be at least 6 characters.");
        }
        this.passwordHash = hash(newPassword, salt);
    }

    // ---- getters ----
    public Role getRole() { return role; }
    public boolean isActive() { return active; }
    public ApprovalStatus getApproval() { return approval; }
    public boolean isApproved() { return approval == ApprovalStatus.APPROVED; }
    public int getId() { return id; }
    public String getUsername() { return username; }
    public String getFullName() { return fullName; }
    public String getEmail() { return email; }
    public LocalDate getMemberSince() { return memberSince; }
    public String getMemberSinceFormatted() { return memberSince.format(DATE_FORMAT); }

    public void setRole(Role role) { this.role = role; }
    public void setActive(boolean active) { this.active = active; }
    public void setApproval(ApprovalStatus approval) { this.approval = approval; }

    // ---- setters (final: safe to call from the constructor) ----
    public final void setFullName(String fullName) {
        if (fullName == null || fullName.isBlank()) {
            throw new IllegalArgumentException("Full name is required.");
        }
        this.fullName = fullName.trim();
    }

    public final void setEmail(String email) {
        if (!isValidEmail(email)) {
            throw new IllegalArgumentException("Please enter a valid email address.");
        }
        this.email = email.trim();
    }

    @Override
    public String toString() {
        return "User{id=" + id + ", username='" + username + "'}";
    }
}
