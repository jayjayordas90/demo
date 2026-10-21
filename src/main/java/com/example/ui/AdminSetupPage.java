package com.example.ui;

import com.example.service.UserService;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;

/** Shown only on the very first run, until an administrator exists. */
public class AdminSetupPage extends AuthPage {

    private final TextField fullName = new TextField();
    private final TextField username = new TextField();
    private final TextField email = new TextField();
    private final PasswordField password = new PasswordField();
    private final PasswordField confirm = new PasswordField();
    private final Label error = UI.errorLabel();

    public AdminSetupPage(Navigator nav) {
        super(nav);
    }

    @Override protected String title() { return "Welcome to PaperKite"; }

    @Override protected String subtitle() {
        return "First-time setup: create the administrator account. "
                + "The administrator approves teachers and manages students and teachers.";
    }

    @Override protected boolean showBackButton() { return false; }

    @Override
    protected Node form() {
        fullName.setPromptText("Administrator's full name");
        username.setPromptText("4-20 letters, numbers, underscore");
        email.setPromptText("admin@example.com");
        password.setPromptText("At least 6 characters");
        confirm.setPromptText("Re-type password");

        Button create = UI.button("Create Administrator Account", true);
        create.setMaxWidth(Double.MAX_VALUE);
        create.setOnAction(e -> attempt());
        confirm.setOnAction(e -> attempt());

        return new VBox(10,
                UI.labeled("Full name", fullName), UI.labeled("Username", username),
                UI.labeled("Email", email), UI.labeled("Password", password),
                UI.labeled("Confirm password", confirm), error, create);
    }

    private void attempt() {
        try {
            UserService.registerFirstAdmin(fullName.getText(), username.getText(),
                    email.getText(), password.getText(), confirm.getText());
            nav.showLogin("Administrator account created. Please log in.");
        } catch (IllegalArgumentException ex) {
            UI.showMessage(error, ex.getMessage(), true);
        }
    }
}
