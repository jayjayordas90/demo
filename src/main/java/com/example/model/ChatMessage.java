package com.example.model;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/** One message in the staff chat. Immutable once sent. */
public class ChatMessage implements Serializable {

    private static final long serialVersionUID = 1L;
    private static final DateTimeFormatter FORMAT = DateTimeFormatter.ofPattern("MMM d, h:mm a");

    private final String senderUsername;
    private final String senderName;
    private final Role senderRole;
    private final String text;
    private final LocalDateTime sentAt;

    public ChatMessage(User sender, String text) {
        this.senderUsername = sender.getUsername();
        this.senderName = sender.getFullName();
        this.senderRole = sender.getRole();
        this.text = text;
        this.sentAt = LocalDateTime.now();
    }

    public boolean isFrom(User user) { return senderUsername.equals(user.getUsername()); }

    public String getSenderName() { return senderName; }
    public Role getSenderRole() { return senderRole; }
    public String getText() { return text; }
    public String getSentFormatted() { return sentAt.format(FORMAT); }
}
