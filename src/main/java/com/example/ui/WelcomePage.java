package com.example.ui;

import com.example.model.User;
import com.example.service.Session;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;

public class WelcomePage extends Page {

    public WelcomePage(Navigator nav) {
        super(nav);
    }

    @Override
    public Parent build() {
        VBox main = new VBox(18);
        main.setAlignment(Pos.CENTER);
        main.setPadding(new Insets(40));
        main.setStyle("-fx-background-color: #f8f8f8;");

        Label logo = new Label("🪁");
        logo.setFont(Font.font(70));

        Label subtitle = new Label("Your voice matters.\nShare safely.");
        subtitle.setFont(Font.font(UI.FONT, 16));
        subtitle.setTextFill(Color.DARKGRAY);
        subtitle.setTextAlignment(TextAlignment.CENTER);

        Button anonymous = UI.button("☁  Continue Anonymously", true);
        Button login = UI.button("👤  Login", false);
        Button create = UI.button("👥  Create Account", false);

        anonymous.setOnAction(e -> {
            Session.start(User.anonymous());
            nav.showHome();
        });
        login.setOnAction(e -> nav.showLogin());
        create.setOnAction(e -> nav.showRegister());

        Label privacy = UI.muted("🔒  Your identity is protected.\nAll reports are kept anonymous.");
        privacy.setTextAlignment(TextAlignment.CENTER);

        main.getChildren().addAll(logo, UI.heading("PAPERKITE", 30), subtitle,
                anonymous, login, create, privacy);
        return main;
    }
}
