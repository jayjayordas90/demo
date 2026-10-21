package com.example.service;

import com.example.model.ChatMessage;
import com.example.model.User;

import java.util.ArrayList;
import java.util.List;

/** The staff chat. Only teachers and administrators may read or write. */
public final class ChatService {

    private static final int MAX_LENGTH = 500;
    private static final List<ChatMessage> MESSAGES = new ArrayList<>();

    private ChatService() { }

    private static void requireStaff(User user) {
        if (user == null || !user.getRole().canUseStaffChat()) {
            throw new IllegalArgumentException("The staff chat is only for teachers and administrators.");
        }
    }

    public static void post(User sender, String text) {
        requireStaff(sender);
        if (text == null || text.isBlank()) {
            throw new IllegalArgumentException("Type a message first.");
        }
        if (text.length() > MAX_LENGTH) {
            throw new IllegalArgumentException("Messages are limited to " + MAX_LENGTH + " characters.");
        }
        MESSAGES.add(new ChatMessage(sender, text.trim()));
        DataStore.save();
    }

    /** Oldest first. Students are refused here, not just hidden in the menu. */
    public static List<ChatMessage> getAll(User viewer) {
        requireStaff(viewer);
        return new ArrayList<>(MESSAGES);
    }

    static ArrayList<ChatMessage> snapshot() { return new ArrayList<>(MESSAGES); }

    static void restore(List<ChatMessage> loaded) {
        MESSAGES.clear();
        MESSAGES.addAll(loaded);
    }
}
