package com.example.ui;

import com.example.model.User;
import com.example.service.Session;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

public class WelcomePage extends Page {

    public WelcomePage(Navigator nav) {
        super(nav);
    }

    @Override
    public Parent build() {
        // soft decorative bubbles behind everything
        Circle bubble1 = bubble(130, "#2E7D8C", 0.09, Pos.TOP_LEFT, -50, -50);
        Circle bubble2 = bubble(180, "#F4A261", 0.12, Pos.BOTTOM_RIGHT, 70, 70);
        Circle bubble3 = bubble(60, "#2E7D8C", 0.10, Pos.BOTTOM_LEFT, 90, -90);

        Node logo = Logo.of(150);
        UI.floatForever(logo);

        Button anonymous = UI.button("Continue Anonymously", "cloud", true);
        Button login = UI.button("Login", "person", false);
        Button create = UI.button("Create Account", "add", false);
        anonymous.setOnAction(e -> {
            Session.start(User.anonymous());
            nav.showHome();
        });
        login.setOnAction(e -> nav.showLogin());
        create.setOnAction(e -> nav.showRegister());

        VBox safe = new VBox(UI.safeBanner("Your identity is protected. All reports are kept anonymous."));
        safe.setMaxWidth(380);

        VBox main = new VBox(16, logo, UI.heading("PaperKite", 40),
                UI.muted("Your voice matters. Share safely."), spacer(), anonymous, login, create, spacer(), safe);
        main.setAlignment(Pos.CENTER);
        main.setPadding(new Insets(30));
        main.setMaxWidth(420);

        StackPane root = new StackPane(bubble1, bubble2, bubble3, main);
        root.getStyleClass().add("auth-bg");
        return root;
    }

    private Node spacer() {
        StackPane gap = new StackPane();
        gap.setMinHeight(4);
        return gap;
    }

    private Circle bubble(double radius, String color, double opacity, Pos position, double dx, double dy) {
        Circle circle = new Circle(radius, Color.web(color, opacity));
        StackPane.setAlignment(circle, position);
        circle.setTranslateX(dx);
        circle.setTranslateY(dy);
        circle.setMouseTransparent(true);
        return circle;
    }
}
