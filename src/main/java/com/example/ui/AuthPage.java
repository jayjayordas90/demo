package com.example.ui;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

/** Shared look for the Login, Registration and Admin-setup pages. */
public abstract class AuthPage extends Page {

    protected AuthPage(Navigator nav) {
        super(nav);
    }

    protected abstract String title();

    protected abstract String subtitle();

    protected abstract Node form();

    /** Subclasses (e.g. first-run admin setup) can hide the Back button. */
    protected boolean showBackButton() {
        return true;
    }

    @Override
    public final Parent build() {
        VBox box = new VBox(14, UI.heading(title(), 26), UI.muted(subtitle()), form());
        if (showBackButton()) {
            Button back = new Button("← Back");
            back.setStyle("-fx-background-color: transparent; -fx-text-fill: gray;");
            back.setOnAction(e -> nav.showWelcome());
            box.getChildren().add(back);
        }
        box.setPadding(new Insets(30));
        box.setMaxWidth(400);
        box.setMaxHeight(Region.USE_PREF_SIZE);
        box.setStyle(UI.CARD_STYLE);

        StackPane root = new StackPane(box);
        root.setAlignment(Pos.CENTER);
        root.setStyle("-fx-background-color: #f8f8f8;");
        return root;
    }
}
