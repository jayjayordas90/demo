package com.example.ui;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

/** Shared look for the Login and Registration pages. */
public abstract class AuthPage extends Page {

    protected AuthPage(Navigator nav) {
        super(nav);
    }

    protected abstract String title();

    protected abstract String subtitle();

    protected abstract Node form();

    @Override
    public final Parent build() {
        Button back = new Button("← Back");
        back.setStyle("-fx-background-color: transparent; -fx-text-fill: gray;");
        back.setOnAction(e -> nav.showWelcome());

        VBox box = new VBox(14, UI.heading(title(), 26), UI.muted(subtitle()), form(), back);
        box.setPadding(new Insets(30));
        box.setMaxWidth(400);
        box.setMaxHeight(javafx.scene.layout.Region.USE_PREF_SIZE);
        box.setStyle(UI.CARD_STYLE);

        StackPane root = new StackPane(box);
        root.setAlignment(Pos.CENTER);
        root.setStyle("-fx-background-color: #f8f8f8;");
        return root;
    }

}
