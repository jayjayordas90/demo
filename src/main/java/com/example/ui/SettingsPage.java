package com.example.ui;

import com.example.model.User;
import com.example.service.DataStore;
import com.example.service.Session;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;

public class SettingsPage extends DashboardPage {

    public SettingsPage(Navigator nav) {
        super(nav);
    }

    @Override protected String navKey() { return "settings"; }

    @Override
    protected Node buildContent() {
        User user = Session.getCurrentUser();

        VBox content = new VBox(14);
        content.setPadding(new Insets(35));
        content.setMaxWidth(500);
        content.getChildren().add(UI.heading("Settings", 28));

        if (user.isAnonymous()) {
            content.getChildren().add(UI.panel("Log in to edit your profile and change your password."));
            return content;
        }

        // ---- profile section ----
        TextField fullName = new TextField(user.getFullName());
        TextField email = new TextField(user.getEmail());
        Label profileMsg = UI.errorLabel();
        Button save = UI.button("Save Changes", true);
        save.setOnAction(e -> {
            try {
                user.setFullName(fullName.getText());
                user.setEmail(email.getText());
                DataStore.save();
                UI.showMessage(profileMsg, "Profile updated.", false);
            } catch (IllegalArgumentException ex) {
                UI.showMessage(profileMsg, ex.getMessage(), true);
            }
        });

        // ---- password section ----
        PasswordField current = new PasswordField();
        PasswordField newPass = new PasswordField();
        PasswordField confirm = new PasswordField();
        Label passMsg = UI.errorLabel();
        Button change = UI.button("Change Password", false);
        change.setOnAction(e -> {
            try {
                if (!newPass.getText().equals(confirm.getText())) {
                    throw new IllegalArgumentException("New passwords do not match.");
                }
                user.changePassword(current.getText(), newPass.getText());
                DataStore.save();
                current.clear();
                newPass.clear();
                confirm.clear();
                UI.showMessage(passMsg, "Password changed.", false);
            } catch (IllegalArgumentException ex) {
                UI.showMessage(passMsg, ex.getMessage(), true);
            }
        });

        content.getChildren().addAll(
                UI.heading("Edit profile", 18),
                UI.labeled("Full name", fullName), UI.labeled("Email", email), profileMsg, save,
                UI.heading("Change password", 18),
                UI.labeled("Current password", current), UI.labeled("New password", newPass),
                UI.labeled("Confirm new password", confirm), passMsg, change);
        return content;
    }
}
