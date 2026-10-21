package com.example.service;

import com.example.model.ChatMessage;
import com.example.model.Report;
import com.example.model.User;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.LinkedHashMap;

/**
 * Saves everything to one file (paperkite-data.dat, next to where the app runs)
 * so accounts, reports and chat survive restarts. Delete that file to start over
 * (the app will ask for a new administrator on the next run).
 */
public final class DataStore {

    private static final Path FILE = Paths.get("paperkite-data.dat");

    /** Everything that gets saved. */
    private static final class Snapshot implements Serializable {
        private static final long serialVersionUID = 1L;

        private final LinkedHashMap<String, User> users;
        private final ArrayList<Report> reports;
        private final ArrayList<ChatMessage> chat;
        private final ArrayList<ActivityLog.Entry> log;

        private Snapshot(LinkedHashMap<String, User> users, ArrayList<Report> reports,
                         ArrayList<ChatMessage> chat, ArrayList<ActivityLog.Entry> log) {
            this.users = users;
            this.reports = reports;
            this.chat = chat;
            this.log = log;
        }
    }

    private DataStore() { }

    public static void load() {
        if (!Files.exists(FILE)) {
            return;
        }
        try (ObjectInputStream in = new ObjectInputStream(Files.newInputStream(FILE))) {
            Snapshot snapshot = (Snapshot) in.readObject();
            UserService.restore(snapshot.users);
            ReportService.restore(snapshot.reports);
            ChatService.restore(snapshot.chat);
            ActivityLog.restore(snapshot.log);
        } catch (IOException | ClassNotFoundException | ClassCastException e) {
            System.err.println("Could not load saved data: " + e.getMessage());
        }
    }

    public static void save() {
        Path temp = Paths.get(FILE + ".tmp");
        try (ObjectOutputStream out = new ObjectOutputStream(Files.newOutputStream(temp))) {
            out.writeObject(new Snapshot(UserService.snapshot(), ReportService.snapshot(),
                    ChatService.snapshot(), ActivityLog.snapshot()));
        } catch (IOException e) {
            System.err.println("Could not save data: " + e.getMessage());
            return;
        }
        try {
            Files.move(temp, FILE, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            System.err.println("Could not save data: " + e.getMessage());
        }
    }
}
