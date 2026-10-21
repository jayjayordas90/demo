package com.example.ui;

import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.layout.VBox;

public class SafetyPage extends DashboardPage {

    public SafetyPage(Navigator nav) {
        super(nav);
    }

    @Override protected String navKey() { return "safety"; }

    @Override
    protected Node buildContent() {
        VBox content = new VBox(20);
        content.setPadding(new Insets(35));
        content.getChildren().addAll(
                UI.heading("Safety & Support", 28),
                UI.muted("If you are experiencing bullying or harassment, "
                        + "remember that you do not have to face it alone."),
                UI.panel("Helpful steps\n\n"
                        + "• Stay in a safe place.\n"
                        + "• Tell a trusted adult, teacher, or school personnel.\n"
                        + "• Keep records of incidents when possible.\n"
                        + "• Use PaperKite to anonymously report an incident."));
        return content;
    }
}
