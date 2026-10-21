package com.example.ui;

import com.example.model.Report;
import javafx.animation.Interpolator;
import javafx.animation.ScaleTransition;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.TextAlignment;
import javafx.util.Duration;

public class ConfirmationPage extends Page {

    private final Report report;

    public ConfirmationPage(Navigator nav, Report report) {
        super(nav);
        this.report = report;
    }

    @Override
    public Parent build() {
        StackPane check = UI.iconBadge("check", "success", 96);
        check.setScaleX(0.2);
        check.setScaleY(0.2);
        ScaleTransition grow = new ScaleTransition(Duration.millis(500), check);
        grow.setToX(1);
        grow.setToY(1);
        grow.setInterpolator(Interpolator.EASE_OUT);
        grow.play();

        Label message = UI.muted("Thank you for speaking up.\nYour report will be reviewed securely by trusted staff.");
        message.setTextAlignment(TextAlignment.CENTER);
        message.setAlignment(Pos.CENTER);

        VBox reference = new VBox(4, UI.muted("Your reference number"), UI.heading(report.getReferenceNo(), 24));
        reference.setAlignment(Pos.CENTER);
        reference.getStyleClass().add("ref-box");
        reference.setMaxWidth(Double.MAX_VALUE);

        Button copy = UI.button("Copy reference number", "reports", false);
        copy.setOnAction(e -> {
            ClipboardContent content = new ClipboardContent();
            content.putString(report.getReferenceNo());
            Clipboard.getSystemClipboard().setContent(content);
            copy.setText("Copied!");
            copy.setGraphic(Icon.of("check"));
        });
        Button another = UI.button("Submit Another Report", "add", true);
        Button home = UI.button("Back to Home", "home", false);
        another.setOnAction(e -> nav.showReportForm("Other"));
        home.setOnAction(e -> nav.showHome());

        VBox card = new VBox(16, check, UI.heading("Your report has been received", 24), message,
                reference, copy, another, home);
        card.setAlignment(Pos.CENTER);
        card.setPadding(new Insets(36));
        card.setMaxWidth(440);
        card.setMaxHeight(javafx.scene.layout.Region.USE_PREF_SIZE);
        card.getStyleClass().add("card");

        StackPane root = new StackPane(card);
        root.getStyleClass().add("auth-bg");
        return root;
    }
}
