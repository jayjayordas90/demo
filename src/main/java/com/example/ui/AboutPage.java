package com.example.ui;

import com.example.PaperKite;
import com.example.model.User;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.layout.VBox;

public class AboutPage extends DashboardPage {

    public AboutPage(Navigator nav) {
        super(nav);
    }

    @Override protected String navKey() { return "about"; }

    @Override
    protected Node buildContent() {
        VBox content = new VBox(20);
        content.setPadding(new Insets(35));
        content.getChildren().addAll(
                UI.heading("About PaperKite", 28),
                UI.muted("PaperKite is a safe place to report bullying and harassment anonymously."),
                UI.panel("Version: " + PaperKite.APP_VERSION + "\n"
                        + "Registered accounts this session: " + User.getTotalUsers()));
        return content;
    }
}
