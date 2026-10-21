package com.example.ui;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

public class SafetyPage extends DashboardPage {

    public SafetyPage(Navigator nav) {
        super(nav);
    }

    @Override protected String navKey() { return "safety"; }

    @Override
    protected Node buildContent() {
        VBox content = new VBox(18);
        content.setPadding(new Insets(32));
        content.getChildren().addAll(
                UI.heading("Safety & Support", 28),
                UI.muted("If you are experiencing bullying or harassment, you do not have to face it alone."),
                step("shield", "green", "Stay in a safe place",
                        "Move away from the situation if you can, and stay near people you trust."),
                step("person", "teal", "Tell a trusted adult",
                        "Teachers, counselors and school staff are there to help you."),
                step("reports", "orange", "Keep a record",
                        "Write down what happened, when and where. It helps the people who support you."),
                step("chat", "violet", "Use PaperKite",
                        "Report anonymously. A caring staff member will review it and can reply to you."),
                UI.safeBanner("Remember: it is not your fault, and asking for help is brave."));
        return content;
    }

    private HBox step(String icon, String tint, String title, String text) {
        VBox words = new VBox(3, UI.heading(title, 16), UI.muted(text));
        HBox row = new HBox(18, UI.iconBadge(icon, tint, 52), words);
        row.setAlignment(Pos.CENTER_LEFT);
        row.setPadding(new Insets(16, 20, 16, 20));
        row.setMaxWidth(720);
        row.getStyleClass().add("card");
        return row;
    }
}
