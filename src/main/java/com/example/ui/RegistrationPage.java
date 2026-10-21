package com.example.ui;

import com.example.model.User;
import com.example.service.Session;
import com.example.service.UserService;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;

public class RegistrationPage extends AuthPage {

    private final TextField fullName = new TextField();
    private final TextField username = new TextField();
    private final TextField email = new TextField();
    private final PasswordField password = new PasswordField();
    private final PasswordField confirm = new PasswordField();
    private final Label error = UI.errorLabel();

    public RegistrationPage(Navigator nav) {
        super(nav);
    }

    @Override protected String title() { return "Create account"; }

    @Override protected String subtitle() { return "Track your reports and manage your profile."; }

    @Override
    protected Node form() {
        fullName.setPromptText("e.g. Juan Dela Cruz");
        username.setPromptText("4-20 letters, numbers, underscore");
        email.setPromptText("you@example.com");
        password.setPromptText("At least 6 characters");
        confirm.setPromptText("Re-type password");

        Button create = UI.button("Create Account", true);
        create.setMaxWidth(Double.MAX_VALUE);
        create.setOnAction(e -> attemptRegister());
        confirm.setOnAction(e -> attemptRegister());

        Button login = new Button("Already have an account? Login");
        login.setStyle("-fx-background-color: transparent; -fx-underline: true;");
        login.setOnAction(e -> nav.showLogin());

        return new VBox(10,
                UI.labeled("Full name", fullName), UI.labeled("Username", username),
                UI.labeled("Email", email), UI.labeled("Password", password),
                UI.labeled("Confirm password", confirm), error, create, login);
    }

    private void attemptRegister() {
        try {
            User user = UserService.register(fullName.getText(), username.getText(),
                    email.getText(), password.getText(), confirm.getText());
            Session.start(user);
            nav.showHome();
        } catch (IllegalArgumentException ex) {
            UI.showMessage(error, ex.getMessage(), true);
        }
    }
}
