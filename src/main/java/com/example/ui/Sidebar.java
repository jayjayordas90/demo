package com.example.ui;

import com.example.model.User;
import com.example.service.Session;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

/** The left navigation menu. */
public class Sidebar {

    private final Navigator nav;
    private final String activeKey;

    public Sidebar(Navigator nav, String activeKey) {
        this.nav = nav;
        this.activeKey = activeKey;
    }

    public VBox build() {
        User user = Session.getCurrentUser();

        VBox sidebar = new VBox(10);
        sidebar.setPadding(new Insets(25, 15, 25, 15));
        sidebar.setPrefWidth(200);
        sidebar.setStyle("-fx-background-color: #f1f1f1;");

        Label logo = UI.heading("🪁\nPAPERKITE", 18);
        logo.setAlignment(Pos.CENTER);
        logo.setMaxWidth(Double.MAX_VALUE);

        Label who = UI.muted(user.isAnonymous() ? "Guest (anonymous)"
                : "@" + user.getUsername() + (user.isStaff() ? " · " + user.getRole() : ""));
        who.setAlignment(Pos.CENTER);
        who.setMaxWidth(Double.MAX_VALUE);

        Region spacer = new Region();
        VBox.setVgrow(spacer, Priority.ALWAYS);

        sidebar.getChildren().addAll(logo, who);

        if (user.getRole().canMonitorReports()) {
            // ---- teacher / administrator menu ----
            sidebar.getChildren().addAll(
                    item("⌂  Dashboard", "home", nav::showHome),
                    item("▣  All Reports", "reports", nav::showAllReports),
                    item("💬  Staff Chat", "chat", nav::showStaffChat));
            if (user.getRole().canManageUsers()) {
                sidebar.getChildren().addAll(
                        item("⚐  Manage Users", "users", nav::showUserManagement),
                        item("≡  Activity Log", "activity", nav::showActivityLog));
            }
            sidebar.getChildren().addAll(
                    item("👤  Profile", "profile", nav::showProfile),
                    spacer,
                    item("⚙  Settings", "settings", nav::showSettings),
                    item("↪  Log out", "logout", nav::logout),
                    item("✕  Exit", "exit", nav::exit));
        } else {
            // ---- student / guest menu ----
            sidebar.getChildren().addAll(
                    item("⌂  Home", "home", nav::showHome),
                    item("▣  My Reports", "reports", nav::showMyReports),
                    item("👤  Profile", "profile", nav::showProfile),
                    item("♡  Safety & Support", "safety", nav::showSafety),
                    item("ⓘ  About", "about", nav::showAbout),
                    spacer,
                    item("⚙  Settings", "settings", nav::showSettings),
                    item(user.isAnonymous() ? "↩  Back to Start" : "↪  Log out", "logout", nav::logout),
                    item("✕  Exit", "exit", nav::exit));
        }
        return sidebar;
    }

    private Button item(String text, String key, Runnable action) {
        Button button = new Button(text);
        button.setMaxWidth(Double.MAX_VALUE);
        button.setAlignment(Pos.CENTER_LEFT);
        boolean active = key.equals(activeKey);
        button.setStyle(active
                ? "-fx-background-color: #222222; -fx-text-fill: white; -fx-font-weight: bold;"
                : "-fx-background-color: transparent;");
        button.setOnAction(e -> action.run());
        return button;
    }
}
