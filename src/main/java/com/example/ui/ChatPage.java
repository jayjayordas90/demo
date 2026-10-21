package com.example.ui;

import com.example.model.ChatMessage;
import com.example.model.Reaction;
import com.example.model.User;
import com.example.service.ChatService;
import com.example.service.Session;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.text.TextFlow;

import java.util.ArrayList;
import java.util.List;

/** Group chat for teachers and administrators ONLY (Navigator and ChatService both enforce this). */
public class ChatPage extends DashboardPage {

    private final VBox messageList = new VBox(10);
    private final HBox pickedBox = new HBox(6);
    private final List<Reaction> picked = new ArrayList<>();

    public ChatPage(Navigator nav) {
        super(nav);
    }

    @Override protected String navKey() { return "chat"; }

    @Override
    protected Node buildContent() {
        User me = Session.getCurrentUser();

        VBox content = new VBox(14);
        content.setPadding(new Insets(32));

        HBox title = new HBox(12, UI.heading("Staff Chat", 28), UI.badge("Staff only", "badge-teacher"));
        title.setAlignment(Pos.CENTER_LEFT);
        content.getChildren().addAll(title,
                UI.muted("A private room for teachers and administrators. Students cannot see it."));

        messageList.setPadding(new Insets(16));
        ScrollPane scroll = new ScrollPane(messageList);
        scroll.setFitToWidth(true);
        scroll.setPrefHeight(380);
        scroll.getStyleClass().add("chat-box");
        // keep the newest message in view whenever the list grows
        messageList.heightProperty().addListener((obs, oldValue, newValue) -> scroll.setVvalue(1.0));
        refreshMessages(me);

        TextField input = new TextField();
        input.setPromptText("Type a message...");
        input.setPrefHeight(42);
        HBox.setHgrow(input, Priority.ALWAYS);
        Button send = UI.button("Send", "send", true);
        send.setPrefWidth(110);
        Label error = UI.errorLabel();

        Runnable sendMessage = () -> {
            try {
                StringBuilder full = new StringBuilder(input.getText().trim());
                if (full.length() > 0 && !picked.isEmpty()) {
                    full.append(' ');
                }
                for (Reaction reaction : picked) {
                    full.append(reaction.getSymbol());
                }
                ChatService.post(me, full.toString());
                input.clear();
                picked.clear();
                redrawPicked();
                UI.hide(error);
                refreshMessages(me);
            } catch (IllegalArgumentException ex) {
                UI.showMessage(error, ex.getMessage(), true);
            }
        };
        send.setOnAction(e -> sendMessage.run());
        input.setOnAction(e -> sendMessage.run());   // Enter sends

        // quick emoji buttons: drawn emoji, added to the message you are writing
        HBox emojis = new HBox(8);
        emojis.setAlignment(Pos.CENTER_LEFT);
        for (Reaction reaction : Reaction.values()) {
            Button emoji = UI.emojiButton(reaction, 26);
            emoji.setOnAction(e -> {
                picked.add(reaction);
                redrawPicked();
                UI.bounce(emoji);
            });
            emojis.getChildren().add(emoji);
        }
        pickedBox.setAlignment(Pos.CENTER_LEFT);
        redrawPicked();

        content.getChildren().addAll(scroll, emojis, pickedBox, new HBox(10, input, send), error);
        return content;
    }

    /** Shows the emoji chosen for the next message (click one to remove it). */
    private void redrawPicked() {
        pickedBox.getChildren().clear();
        pickedBox.setVisible(!picked.isEmpty());
        pickedBox.setManaged(!picked.isEmpty());
        for (Reaction reaction : new ArrayList<>(picked)) {
            Button chip = new Button();
            chip.setGraphic(Emoji.of(reaction, 24));
            chip.getStyleClass().add("btn-ghost");
            chip.setTooltip(new Tooltip("Click to remove"));
            chip.setOnAction(e -> {
                picked.remove(reaction);
                redrawPicked();
            });
            pickedBox.getChildren().add(chip);
        }
    }

    private void refreshMessages(User me) {
        messageList.getChildren().clear();
        List<ChatMessage> messages = ChatService.getAll(me);
        if (messages.isEmpty()) {
            messageList.getChildren().add(UI.muted("No messages yet. Say hello to your colleagues!"));
            return;
        }
        for (ChatMessage message : messages) {
            messageList.getChildren().add(bubble(message, message.isFrom(me)));
        }
    }

    private HBox bubble(ChatMessage message, boolean mine) {
        Label header = UI.muted(message.getSenderName() + " (" + message.getSenderRole() + ") · "
                + message.getSentFormatted());
        header.setStyle("-fx-font-size: 11px;");

        // the six reaction emoji inside the text are drawn as vector art, not font characters
        TextFlow text = Emoji.flow(message.getText(), 22, mine
                ? "-fx-fill: white; -fx-font-size: 14px;"
                : "-fx-fill: #000000; -fx-font-size: 14px;");
        text.getStyleClass().add(mine ? "bubble-mine" : "bubble-other");
        text.setMaxWidth(400);

        VBox box = new VBox(3, header, text);
        box.setAlignment(mine ? Pos.CENTER_RIGHT : Pos.CENTER_LEFT);

        HBox row = new HBox(box);
        row.setAlignment(mine ? Pos.CENTER_RIGHT : Pos.CENTER_LEFT);
        return row;
    }
}
