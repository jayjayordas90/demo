package com.example.ui;

import com.example.model.User;
import com.example.service.ReportService;
import com.example.service.Session;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

public class ProfilePage extends DashboardPage {

    public ProfilePage(Navigator nav) {
        super(nav);
    }

    @Override protected String navKey() { return "profile"; }

    @Override
    protected Node buildContent() {
        User user = Session.getCurrentUser();

        VBox content = new VBox(20);
        content.setPadding(new Insets(35));
        content.getChildren().add(UI.heading("My Profile", 28));

        if (user.isAnonymous()) {
            Button login = UI.button("Login", true);
            Button register = UI.button("Create Account", false);
            login.setOnAction(e -> nav.showLogin());
            register.setOnAction(e -> nav.showRegister());
            content.getChildren().addAll(
                    UI.panel("You are browsing as an anonymous guest, so there is no profile to show."),
                    login, register);
            return content;
        }

        // Avatar: a circle with the user's first initial
        Label initial = new Label(user.getFullName().substring(0, 1).toUpperCase());
        initial.setFont(Font.font(UI.FONT, FontWeight.BOLD, 32));
        initial.setTextFill(Color.WHITE);
        StackPane avatar = new StackPane(new Circle(38, Color.web("#222222")), initial);

        VBox names = new VBox(4, UI.heading(user.getFullName(), 22), UI.muted("@" + user.getUsername()));
        names.setAlignment(Pos.CENTER_LEFT);
        HBox header = new HBox(20, avatar, names);
        header.setAlignment(Pos.CENTER_LEFT);

        FlowPane stats = new FlowPane(15, 15,
                UI.stat(String.valueOf(ReportService.countFor(user)), "Reports submitted"),
                UI.stat(user.getMemberSinceFormatted(), "Member since"));

        Button edit = UI.button("✎  Edit Profile / Change Password", true);
        edit.setOnAction(e -> nav.showSettings());

        content.getChildren().addAll(header,
                UI.panel("Email:  " + user.getEmail() + "\nUsername:  " + user.getUsername()
                        + "\nAccount ID:  " + user.getId()),
                stats, edit);
        return content;
    }
}
