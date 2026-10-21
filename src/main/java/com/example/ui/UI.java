package com.example.ui;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.control.Button;
import javafx.scene.control.Control;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.TextAlignment;

/** Static helper factory for reusable styled controls. Cannot be instantiated. */
public final class UI {

    public static final String FONT = "Arial";
    public static final String PANEL_STYLE = "-fx-background-color: #eeeeee; -fx-background-radius: 8;";
    public static final String CARD_STYLE = "-fx-background-color: white; -fx-border-color: #dddddd;"
            + "-fx-border-radius: 6; -fx-background-radius: 6;";

    private UI() { }

    public static Label heading(String text, int size) {
        Label label = new Label(text);
        label.setFont(Font.font(FONT, FontWeight.BOLD, size));
        return label;
    }

    public static Label muted(String text) {
        Label label = new Label(text);
        label.setTextFill(Color.GRAY);
        label.setWrapText(true);
        return label;
    }

    public static Label panel(String text) {
        Label label = new Label(text);
        label.setWrapText(true);
        label.setPadding(new Insets(20));
        label.setStyle(PANEL_STYLE);
        return label;
    }

    public static Label errorLabel() {
        Label label = new Label();
        label.setTextFill(Color.CRIMSON);
        label.setWrapText(true);
        hide(label);
        return label;
    }

    public static void showMessage(Label label, String message, boolean error) {
        label.setText(message);
        label.setTextFill(error ? Color.CRIMSON : Color.SEAGREEN);
        label.setVisible(true);
        label.setManaged(true);
    }

    public static void hide(Label label) {
        label.setVisible(false);
        label.setManaged(false);
    }

    public static Button button(String text, boolean dark) {
        Button button = new Button(text);
        styleButton(button, dark);
        return button;
    }

    public static void styleButton(Button button, boolean dark) {
        button.setPrefWidth(280);
        button.setPrefHeight(42);
        button.setCursor(Cursor.HAND);
        if (dark) {
            button.setStyle("-fx-background-color: #222222; -fx-text-fill: white;"
                    + "-fx-font-weight: bold; -fx-background-radius: 5;");
        } else {
            button.setStyle("-fx-background-color: white; -fx-text-fill: black;"
                    + "-fx-border-color: #cccccc; -fx-border-radius: 5; -fx-background-radius: 5;");
        }
    }

    /** A label stacked above an input control. */
    public static VBox labeled(String labelText, Control input) {
        Label label = new Label(labelText);
        label.setFont(Font.font(FONT, FontWeight.BOLD, 12));
        if (input instanceof TextField field) {
            field.setPrefHeight(36);
        }
        input.setMaxWidth(Double.MAX_VALUE);
        return new VBox(5, label, input);
    }

    /** A clickable dashboard card. */
    public static VBox card(String icon, String title, String description) {
        VBox card = new VBox(10);
        card.setAlignment(Pos.CENTER);
        card.setPadding(new Insets(20));
        card.setPrefSize(210, 150);
        card.setStyle(CARD_STYLE);
        card.setCursor(Cursor.HAND);

        Label iconLabel = new Label(icon);
        iconLabel.setFont(Font.font(30));
        Label descriptionLabel = new Label(description);
        descriptionLabel.setWrapText(true);
        descriptionLabel.setTextAlignment(TextAlignment.CENTER);

        card.getChildren().addAll(iconLabel, heading(title, 15), descriptionLabel);
        return card;
    }

    /** A small number + caption box for the dashboard summary row. */
    public static VBox stat(String value, String caption) {
        VBox box = new VBox(4, heading(value, 22), muted(caption));
        box.setPadding(new Insets(15, 25, 15, 25));
        box.setStyle(CARD_STYLE);
        return box;
    }
}
