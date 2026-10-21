package com.example.ui;

import com.example.model.User;
import com.example.service.ReportService;
import com.example.service.Session;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

public class ProfilePage extends DashboardPage {

    public ProfilePage(Navigator nav) {
        super(nav);
    }

    @Override protected String navKey() { return "profile"; }

    @Override
    protected Node buildContent() {
        User user = Session.getCurrentUser();

        VBox content = new VBox(20);
        content.setPadding(new Insets(32));
        content.getChildren().add(UI.heading("My Profile", 28));

        if (user.isAnonymous()) {
            Button login = UI.button("Login", "person", true);
            Button register = UI.button("Create Account", "add", false);
            login.setOnAction(e -> nav.showLogin());
            register.setOnAction(e -> nav.showRegister());
            content.getChildren().addAll(
                    UI.panel("You are browsing as an anonymous guest, so there is no profile to show."),
                    login, register);
            return content;
        }

        VBox names = new VBox(6, UI.heading(user.getFullName(), 24), UI.muted("@" + user.getUsername()),
                UI.roleBadge(user.getRole()));
        names.setAlignment(Pos.CENTER_LEFT);
        HBox header = new HBox(20, UI.avatar(user.getFullName(), 38), names);
        header.setAlignment(Pos.CENTER_LEFT);
        header.setPadding(new Insets(24));
        header.setMaxWidth(720);
        header.getStyleClass().add("card");

        VBox details = new VBox(14,
                infoRow("reports", "Email", user.getEmail()),
                infoRow("person", "Username", user.getUsername()),
                infoRow("shield", "Role", user.getRole().getLabel()),
                infoRow("history", "Member since", user.getMemberSinceFormatted()));
        details.setPadding(new Insets(22));
        details.setMaxWidth(720);
        details.getStyleClass().add("card");

        content.getChildren().addAll(header, details);
        if (!user.isStaff()) {
            content.getChildren().add(new FlowPane(16, 16,
                    UI.stat(String.valueOf(ReportService.countFor(user)), "Reports submitted")));
        }

        Button edit = UI.button("Edit Profile / Change Password", "edit", true);
        edit.setOnAction(e -> nav.showSettings());
        content.getChildren().add(edit);
        return content;
    }

    private HBox infoRow(String icon, String label, String value) {
        HBox row = new HBox(14, UI.iconBadge(icon, "teal", 40),
                new VBox(1, UI.muted(label), UI.heading(value, 14)));
        row.setAlignment(Pos.CENTER_LEFT);
        return row;
    }
}
