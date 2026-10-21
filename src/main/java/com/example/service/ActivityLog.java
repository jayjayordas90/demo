package com.example.service;

import com.example.model.User;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/** Audit trail of staff actions (static, saved with DataStore). */
public final class ActivityLog {

    /** Static nested class: one log line. */
    public static final class Entry implements Serializable {
        private static final long serialVersionUID = 1L;

        private final String time;
        private final String actor;
        private final String action;

        private Entry(String actor, String action) {
            this.time = LocalDateTime.now().format(FORMAT);
            this.actor = actor;
            this.action = action;
        }

        public String getTime() { return time; }
        public String getActor() { return actor; }
        public String getAction() { return action; }
    }

    private static final DateTimeFormatter FORMAT = DateTimeFormatter.ofPattern("MMM d, yyyy h:mm a");
    private static final List<Entry> ENTRIES = new ArrayList<>();

    private ActivityLog() { }

    public static void record(User actor, String action) {
        ENTRIES.add(new Entry(actor.getUsername() + " (" + actor.getRole() + ")", action));
        DataStore.save();
    }

    /** Newest first. */
    public static List<Entry> getAll() {
        List<Entry> copy = new ArrayList<>(ENTRIES);
        Collections.reverse(copy);
        return copy;
    }

    // package-private: only DataStore uses these
    static ArrayList<Entry> snapshot() { return new ArrayList<>(ENTRIES); }

    static void restore(List<Entry> loaded) {
        ENTRIES.clear();
        ENTRIES.addAll(loaded);
    }
}
