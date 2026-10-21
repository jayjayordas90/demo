package com.example.ui;

import com.example.model.User;
import com.example.service.Session;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

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

        VBox sidebar = new VBox(6);
        sidebar.getStyleClass().add("sidebar");
        sidebar.setPadding(new Insets(24, 14, 20, 14));
        sidebar.setPrefWidth(240);
        sidebar.setMinWidth(240);

        HBox brand = new HBox(10, Logo.of(52), UI.heading("PaperKite", 20));
        brand.setAlignment(Pos.CENTER_LEFT);
        brand.setPadding(new Insets(0, 0, 14, 6));

        sidebar.getChildren().addAll(brand, userChip(user));
        sidebar.getChildren().add(spacerOf(8));

        Region spacer = new Region();
        VBox.setVgrow(spacer, Priority.ALWAYS);

        if (user.getRole().canMonitorReports()) {
            // ---- teacher / administrator menu ----
            sidebar.getChildren().addAll(
                    item("Dashboard", "dashboard", "home", nav::showHome),
                    item("All Reports", "reports", "reports", nav::showAllReports),
                    item("Staff Chat", "chat", "chat", nav::showStaffChat));
            if (user.getRole().canManageUsers()) {
                sidebar.getChildren().addAll(
                        item("Manage Users", "people", "users", nav::showUserManagement),
                        item("Activity Log", "history", "activity", nav::showActivityLog));
            }
            sidebar.getChildren().addAll(
                    item("Profile", "person", "profile", nav::showProfile),
                    spacer,
                    note("Reports are confidential. Handle them with care."),
                    item("Settings", "settings", "settings", nav::showSettings),
                    item("Log out", "logout", "logout", nav::logout),
                    item("Exit", "close", "exit", nav::exit));
        } else {
            // ---- student / guest menu ----
            sidebar.getChildren().addAll(
                    item("Home", "home", "home", nav::showHome),
                    item("My Reports", "reports", "reports", nav::showMyReports),
                    item("Profile", "person", "profile", nav::showProfile),
                    item("Safety & Support", "heart", "safety", nav::showSafety),
                    item("About", "info", "about", nav::showAbout),
                    spacer,
                    note("Your identity is protected. Reports stay anonymous."),
                    item("Settings", "settings", "settings", nav::showSettings),
                    item(user.isAnonymous() ? "Back to Start" : "Log out", "logout", "logout", nav::logout),
                    item("Exit", "close", "exit", nav::exit));
        }
        return sidebar;
    }

    private HBox userChip(User user) {
        String display = user.isAnonymous() ? "Guest" : user.getFullName();
        String sub = user.isAnonymous() ? "Anonymous"
                : (user.isStaff() ? user.getRole().getLabel() : "@" + user.getUsername());

        Circle circle = new Circle(18, Color.WHITE);
        Label letter = new Label(display.substring(0, 1).toUpperCase());
        letter.setStyle("-fx-text-fill: #1B5662; -fx-font-weight: bold; -fx-font-size: 16px;");
        Label name = new Label(display);
        name.setStyle("-fx-font-weight: bold;");

        HBox chip = new HBox(10, new StackPane(circle, letter), new VBox(1, name, UI.muted(sub)));
        chip.getStyleClass().add("user-chip");
        chip.setAlignment(Pos.CENTER_LEFT);
        return chip;
    }

    private Region spacerOf(double height) {
        Region gap = new Region();
        gap.setMinHeight(height);
        gap.setPrefHeight(height);
        return gap;
    }

    private HBox note(String text) {
        Label label = new Label(text);
        label.setWrapText(true);
        HBox note = new HBox(10, new Icon("lock", 18), label);
        note.getStyleClass().add("sidebar-note");
        note.setAlignment(Pos.CENTER_LEFT);
        return note;
    }

    private Button item(String text, String iconName, String key, Runnable action) {
        Button button = new Button(text, Icon.of(iconName));
        button.getStyleClass().add("nav-button");
        if (key.equals(activeKey)) {
            button.getStyleClass().add("active");
        }
        button.setMaxWidth(Double.MAX_VALUE);
        button.setOnAction(e -> action.run());
        return button;
    }
}
