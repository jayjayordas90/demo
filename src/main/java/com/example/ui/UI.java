package com.example.ui;

import com.example.model.Report;
import com.example.model.Role;
import javafx.animation.Animation;
import javafx.animation.FadeTransition;
import javafx.animation.Interpolator;
import javafx.animation.ParallelTransition;
import javafx.animation.ScaleTransition;
import javafx.animation.SequentialTransition;
import javafx.animation.TranslateTransition;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Control;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.TextAlignment;
import javafx.util.Duration;

/** Static factory for the app's reusable styled controls and little animations. */
public final class UI {

    private UI() { }

    // ---------- text ----------

    public static Label heading(String text, int size) {
        Label label = new Label(text);
        label.getStyleClass().add("heading");
        label.setStyle("-fx-font-size: " + size + "px;");
        return label;
    }

    public static Label muted(String text) {
        Label label = new Label(text);
        label.getStyleClass().add("muted");
        label.setWrapText(true);
        return label;
    }

    /** A soft rounded note box. */
    public static Label panel(String text) {
        Label label = new Label(text);
        label.getStyleClass().add("note");
        label.setWrapText(true);
        label.setMaxWidth(Double.MAX_VALUE);
        return label;
    }

    public static Label errorLabel() {
        Label label = new Label();
        label.setWrapText(true);
        hide(label);
        return label;
    }

    public static void showMessage(Label label, String message, boolean error) {
        label.setText(message);
        label.getStyleClass().removeAll("msg-error", "msg-ok");
        label.getStyleClass().add(error ? "msg-error" : "msg-ok");
        label.setVisible(true);
        label.setManaged(true);
    }

    public static void hide(Label label) {
        label.setVisible(false);
        label.setManaged(false);
    }

    // ---------- buttons ----------

    public static Button button(String text, boolean primary) {
        return button(text, null, primary);
    }

    public static Button button(String text, String iconName, boolean primary) {
        Button button = new Button(text);
        button.getStyleClass().add(primary ? "btn-primary" : "btn-secondary");
        if (iconName != null) {
            button.setGraphic(Icon.of(iconName));
        }
        button.setPrefWidth(280);
        button.setPrefHeight(42);
        return button;
    }

    /** Flat text button; danger = red. */
    public static Button flat(String text, String iconName, boolean danger) {
        Button button = new Button(text);
        button.getStyleClass().add(danger ? "btn-danger" : "btn-ghost");
        if (iconName != null) {
            button.setGraphic(Icon.of(iconName));
        }
        return button;
    }

    /** A button showing a drawn emoji instead of text. */
    public static Button emojiButton(com.example.model.Reaction reaction, double size) {
        Button button = new Button();
        button.setGraphic(Emoji.of(reaction, size));
        button.getStyleClass().add("emoji-button");
        button.setTooltip(new javafx.scene.control.Tooltip(reaction.getMeaning()));
        pop(button);
        return button;
    }

    // ---------- inputs ----------

    /** A label stacked above an input control. */
    public static VBox labeled(String labelText, Control input) {
        Label label = new Label(labelText);
        label.getStyleClass().add("field-label");
        if (input instanceof TextField field) {
            field.setPrefHeight(40);
        }
        input.setMaxWidth(Double.MAX_VALUE);
        return new VBox(6, label, input);
    }

    // ---------- cards, badges, banners ----------

    public static StackPane iconBadge(String iconName, String tint, double size) {
        StackPane badge = new StackPane(new Icon(iconName, size * 0.5));
        badge.getStyleClass().addAll("icon-badge", "tint-" + tint);
        badge.setMinSize(size, size);
        badge.setPrefSize(size, size);
        badge.setMaxSize(size, size);
        badge.setStyle("-fx-background-radius: " + (size / 2) + ";");
        return badge;
    }

    /** A clickable dashboard card that lifts when hovered. */
    public static VBox card(String iconName, String tint, String title, String description) {
        VBox card = new VBox(10);
        card.setAlignment(Pos.CENTER);
        card.setPadding(new Insets(22, 18, 22, 18));
        card.setPrefSize(220, 170);
        card.getStyleClass().addAll("card", "card-hover");
        card.setStyle("-fx-cursor: hand;");

        Label descriptionLabel = muted(description);
        descriptionLabel.setTextAlignment(TextAlignment.CENTER);
        descriptionLabel.setAlignment(Pos.CENTER);

        card.getChildren().addAll(iconBadge(iconName, tint, 54), heading(title, 15), descriptionLabel);
        lift(card);
        return card;
    }

    /** A number + caption box for summary rows. */
    public static VBox stat(String value, String caption) {
        Label number = heading(value, 26);
        number.getStyleClass().add("stat-value");
        VBox box = new VBox(2, number, muted(caption));
        box.getStyleClass().addAll("card", "stat-card");
        box.setMinWidth(150);
        return box;
    }

    /** The green "you are safe here" banner. */
    public static HBox safeBanner(String text) {
        Label label = new Label(text);
        label.setWrapText(true);
        HBox banner = new HBox(12, new Icon("shield", 24), label);
        banner.setAlignment(Pos.CENTER_LEFT);
        banner.getStyleClass().add("safe-banner");
        return banner;
    }

    public static HBox statusBadge(Report.Status status) {
        String style = switch (status) {
            case SUBMITTED -> "badge-submitted";
            case UNDER_REVIEW -> "badge-review";
            case RESOLVED -> "badge-resolved";
        };
        return badge(status.getLabel(), style);
    }

    public static HBox roleBadge(Role role) {
        String style = switch (role) {
            case STUDENT -> "badge-student";
            case TEACHER -> "badge-teacher";
            case ADMIN -> "badge-admin";
        };
        return badge(role.getLabel(), style);
    }

    public static HBox badge(String text, String styleClass) {
        HBox badge = new HBox(new Label(text));
        badge.getStyleClass().addAll("badge", styleClass);
        badge.setAlignment(Pos.CENTER);
        badge.setMaxWidth(javafx.scene.layout.Region.USE_PREF_SIZE);
        return badge;
    }


    /** A round avatar showing someone's first initial. */
    public static StackPane avatar(String name, double radius) {
        String initial = name == null || name.isBlank() ? "?" : name.substring(0, 1).toUpperCase();
        Circle circle = new Circle(radius, Color.BLACK);
        Label letter = new Label(initial);
        letter.setStyle("-fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: " + (radius * 0.95) + "px;");
        return new StackPane(circle, letter);
    }

    // ---------- little animations ----------

    /** Rises slightly while the mouse is over the node. */
    public static void lift(Node node) {
        TranslateTransition up = new TranslateTransition(Duration.millis(140), node);
        up.setToY(-6);
        TranslateTransition down = new TranslateTransition(Duration.millis(140), node);
        down.setToY(0);
        node.setOnMouseEntered(e -> {
            down.stop();
            up.playFromStart();
        });
        node.setOnMouseExited(e -> {
            up.stop();
            down.playFromStart();
        });
    }

    /** Grows a little on hover and bounces when clicked. */
    public static void pop(Node node) {
        ScaleTransition grow = new ScaleTransition(Duration.millis(120), node);
        grow.setToX(1.15);
        grow.setToY(1.15);
        ScaleTransition shrink = new ScaleTransition(Duration.millis(120), node);
        shrink.setToX(1.0);
        shrink.setToY(1.0);
        node.setOnMouseEntered(e -> {
            shrink.stop();
            grow.playFromStart();
        });
        node.setOnMouseExited(e -> {
            grow.stop();
            shrink.playFromStart();
        });
    }

    /** A quick "boing" used when something is sent. */
    public static void bounce(Node node) {
        ScaleTransition up = new ScaleTransition(Duration.millis(110), node);
        up.setToX(1.4);
        up.setToY(1.4);
        ScaleTransition down = new ScaleTransition(Duration.millis(140), node);
        down.setToX(1.0);
        down.setToY(1.0);
        new SequentialTransition(up, down).play();
    }

    /** Fades and slides a node in (used when pages change). */
    public static void fadeIn(Node node) {
        node.setOpacity(0);
        node.setTranslateY(14);
        FadeTransition fade = new FadeTransition(Duration.millis(300), node);
        fade.setToValue(1);
        TranslateTransition slide = new TranslateTransition(Duration.millis(300), node);
        slide.setToY(0);
        slide.setInterpolator(Interpolator.EASE_OUT);
        new ParallelTransition(fade, slide).play();
    }

    /** Gentle endless bobbing (stops by itself when the node leaves the screen). */
    public static void floatForever(Node node) {
        TranslateTransition bob = new TranslateTransition(Duration.seconds(2.2), node);
        bob.setFromY(0);
        bob.setToY(-12);
        bob.setAutoReverse(true);
        bob.setCycleCount(Animation.INDEFINITE);
        bob.setInterpolator(Interpolator.EASE_BOTH);
        node.sceneProperty().addListener((obs, oldScene, newScene) -> {
            if (newScene == null) {
                bob.stop();
            }
        });
        bob.play();
    }
}
