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

public class LoginPage extends AuthPage {

    private final TextField username = new TextField();
    private final PasswordField password = new PasswordField();
    private final Label error = UI.errorLabel();
    private final Label notice = UI.errorLabel();
    private final String noticeText;

    public LoginPage(Navigator nav, String noticeText) {
        super(nav);
        this.noticeText = noticeText;
    }

    @Override protected String title() { return "Welcome back"; }

    @Override protected String subtitle() { return "Log in to continue."; }

    @Override
    protected Node form() {
        if (noticeText != null) {
            UI.showMessage(notice, noticeText, false);
        }
        username.setPromptText("Your username");
        password.setPromptText("Your password");

        Button login = UI.button("Login", true);
        login.setMaxWidth(Double.MAX_VALUE);
        login.setOnAction(e -> attemptLogin());
        password.setOnAction(e -> attemptLogin());   // pressing Enter submits

        Button register = new Button("No account yet? Create one");
        register.setStyle("-fx-background-color: transparent; -fx-underline: true;");
        register.setOnAction(e -> nav.showRegister());

        return new VBox(12, notice, UI.labeled("Username", username), UI.labeled("Password", password),
                error, login, register);
    }

    private void attemptLogin() {
        try {
            User user = UserService.login(username.getText(), password.getText());
            Session.start(user);
            nav.showHome();
        } catch (IllegalArgumentException ex) {
            UI.hide(notice);
            UI.showMessage(error, ex.getMessage(), true);
        }
    }
}
