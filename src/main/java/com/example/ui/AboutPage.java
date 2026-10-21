package com.example.ui;

import com.example.PaperKite;
import com.example.model.User;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.layout.VBox;

public class AboutPage extends DashboardPage {

    public AboutPage(Navigator nav) {
        super(nav);
    }

    @Override protected String navKey() { return "about"; }

    @Override
    protected Node buildContent() {
        Node logo = Logo.of(120);
        UI.floatForever(logo);

        VBox card = new VBox(14, logo, UI.heading("About PaperKite", 28),
                UI.muted("PaperKite is a safe, kind place to report bullying and harassment anonymously, "
                        + "so everyone can feel heard and protected."),
                UI.panel("Version: " + PaperKite.APP_VERSION + "\n"
                        + "Accounts created: " + User.getTotalUsers()));
        card.setAlignment(Pos.CENTER_LEFT);
        card.setPadding(new Insets(30));
        card.setMaxWidth(640);
        card.getStyleClass().add("card");

        VBox content = new VBox(card);
        content.setPadding(new Insets(32));
        return content;
    }
}
