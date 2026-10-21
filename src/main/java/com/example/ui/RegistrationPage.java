package com.example.ui;

import com.example.model.Role;
import com.example.model.User;
import com.example.service.Session;
import com.example.service.UserService;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
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
    private final ComboBox<Role> roleBox = new ComboBox<>();
    private final Label teacherHint = UI.muted("Teacher accounts must be approved by an administrator "
            + "before you can log in.");
    private final Label error = UI.errorLabel();

    public RegistrationPage(Navigator nav) {
        super(nav);
    }

    @Override protected String title() { return "Create account"; }

    @Override protected String subtitle() { return "Track your reports, or join as a teacher to help."; }

    @Override
    protected Node form() {
        fullName.setPromptText("e.g. Juan Dela Cruz");
        username.setPromptText("4-20 letters, numbers, underscore");
        email.setPromptText("you@example.com");
        password.setPromptText("At least 6 characters");
        confirm.setPromptText("Re-type password");

        roleBox.getItems().addAll(Role.STUDENT, Role.TEACHER);
        roleBox.setValue(Role.STUDENT);
        UI.hide(teacherHint);
        roleBox.setOnAction(e -> {
            boolean teacher = roleBox.getValue() == Role.TEACHER;
            teacherHint.setVisible(teacher);
            teacherHint.setManaged(teacher);
        });

        Button create = UI.button("Create Account", true);
        create.setMaxWidth(Double.MAX_VALUE);
        create.setOnAction(e -> attemptRegister());
        confirm.setOnAction(e -> attemptRegister());

        Button login = new Button("Already have an account? Login");
        login.setStyle("-fx-background-color: transparent; -fx-underline: true;");
        login.setOnAction(e -> nav.showLogin());

        return new VBox(10,
                UI.labeled("I am a...", roleBox), teacherHint,
                UI.labeled("Full name", fullName), UI.labeled("Username", username),
                UI.labeled("Email", email), UI.labeled("Password", password),
                UI.labeled("Confirm password", confirm), error, create, login);
    }

    private void attemptRegister() {
        try {
            Role role = roleBox.getValue();
            User user = UserService.register(fullName.getText(), username.getText(),
                    email.getText(), password.getText(), confirm.getText(), role);
            if (role == Role.TEACHER) {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Registration submitted");
                alert.setHeaderText("Waiting for approval");
                alert.setContentText("Your registration will be approved by an admin. "
                        + "Please try logging in later.");
                alert.showAndWait();
                nav.showLogin();
            } else {
                Session.start(user);
                nav.showHome();
            }
        } catch (IllegalArgumentException ex) {
            UI.showMessage(error, ex.getMessage(), true);
        }
    }
}
