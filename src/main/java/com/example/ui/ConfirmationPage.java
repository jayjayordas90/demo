package com.example.ui;

import com.example.model.Report;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;

public class ConfirmationPage extends Page {

    private final Report report;

    public ConfirmationPage(Navigator nav, Report report) {
        super(nav);
        this.report = report;
    }

    @Override
    public Parent build() {
        VBox content = new VBox(20);
        content.setAlignment(Pos.CENTER);
        content.setPadding(new Insets(40));
        content.setStyle("-fx-background-color: #f8f8f8;");

        Label check = new Label("✓");
        check.setFont(Font.font(60));

        Label message = new Label("Thank you for speaking up.\nYour report will be reviewed securely.");
        message.setTextAlignment(TextAlignment.CENTER);

        Button another = UI.button("Submit Another Report", true);
        Button home = UI.button("Back to Home", false);
        another.setOnAction(e -> nav.showReportForm("Other"));
        home.setOnAction(e -> nav.showHome());

        content.getChildren().addAll(check, UI.heading("Your report has been received.", 26), message,
                UI.heading("Reference Number: " + report.getReferenceNo(), 16), another, home);
        return content;
    }
}
