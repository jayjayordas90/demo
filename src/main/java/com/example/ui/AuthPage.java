package com.example.ui;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
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
        StackPane logo = new StackPane(Logo.of(80));
        logo.setAlignment(Pos.CENTER_LEFT);

        VBox box = new VBox(14, logo, UI.heading(title(), 26), UI.muted(subtitle()), form());
        if (showBackButton()) {
            Button back = UI.flat("Back", "back", false);
            back.setOnAction(e -> nav.showWelcome());
            box.getChildren().add(back);
        }
        box.setPadding(new Insets(32));
        box.setMaxWidth(430);
        box.setMaxHeight(Region.USE_PREF_SIZE);
        box.getStyleClass().add("card");

        // scrolls on small windows so long forms (registration) never get cut off
        StackPane center = new StackPane(box);
        center.setAlignment(Pos.CENTER);
        center.setPadding(new Insets(24));
        ScrollPane scroll = new ScrollPane(center);
        scroll.setFitToWidth(true);
        scroll.setFitToHeight(true);

        StackPane root = new StackPane(scroll);
        root.getStyleClass().add("auth-bg");
        return root;
    }
}
